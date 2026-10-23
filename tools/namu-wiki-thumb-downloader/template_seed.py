#!/usr/bin/env python3
"""
나무위키 캐릭터 문서 URL에서 대표 이미지를 뽑아 1:1 PNG로 저장한다.

사용법: python3 tools/template_seed.py
  1) URL> 에 나무위키 주소를 하나씩 붙여넣고, 다 넣었으면 exit 를 입력한다.
  2) 도구가 문서마다 후보 사진을 점수로 고르고, 브라우저로 검토 화면(tools/review.html)을 연다.
  3) 화면에서 틀린 사진은 다른 후보를 클릭해 바꾸거나 제외하고, 저장을 누르면 tools/output/ 에 저장한다.

표준 라이브러리와 맥 내장 sips만 쓴다.
"""
import base64
import html
import json
import re
import shutil
import subprocess
import sys
import tempfile
import threading
import time
import urllib.parse
import urllib.request
import webbrowser
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

TOOLS_DIR = Path(__file__).resolve().parent
OUTPUT_DIR = TOOLS_DIR / "output"
REVIEW_PAGE = TOOLS_DIR / "review.html"
USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/120 Safari/537.36"
REQUEST_INTERVAL_SECONDS = 1.5
IMAGE_DOWNLOAD_PARALLEL = 4
PORT_START = 8765
PORT_TRIES = 20
MAX_SIDE = 1024
MIN_IMAGE_SIDE = 200
MAX_TABLE_IMAGES = 8
MAX_CANDIDATES = 5
TITLE_WINDOW = 400
ENGLISH_WINDOW = 120
HIGH_SCORE = 8
PROFILE_ALT_SCORE = 9
ENGLISH_NAME = re.compile(r"[A-Z][A-Za-z']+(?: [A-Z][A-Za-z']+){0,3}")
INFOBOX_LABELS = re.compile(r"본명|출생|생일|성우|종족|소속|국적|성별|신장|키|나이|데뷔|제작|출신|분류|모델|이명|별명|직업|능력|이름|활동|팬덤|구독자|혈액형|취미")
URL_PREFIX = "https://namu.wiki/w/"
CONTENT_TYPES = {"webp": "image/webp", "png": "image/png", "jpg": "image/jpeg", "jpeg": "image/jpeg", "gif": "image/gif"}


def fetch(url, referer=None):
    headers = {"User-Agent": USER_AGENT}

    if referer:
        headers["Referer"] = referer

    # 한글, 공백이 풀린 주소도 받을 수 있게 다시 인코딩한다 (이미 인코딩된 %XX는 그대로 둔다)
    request = urllib.request.Request(urllib.parse.quote(url, safe=":/%?=&#"), headers=headers)

    with urllib.request.urlopen(request, timeout=30) as response:
        return response.read()


def find_title(page):
    match = re.search(r"<title>(.*?)</title>", page)

    if not match:
        return None

    return html.unescape(match.group(1)).replace(" - 나무위키", "").strip()


def to_slug(text):
    return re.sub(r"[^a-z0-9]+", "-", text.lower()).strip("-")


def placeholder_side(tag):
    # 지연 로딩 자리표시 svg의 width, height로 원본 크기를 알아낸다
    match = re.search(r"src='data:image/svg\+xml;base64,([^']+)'", tag)

    if not match:
        return 0

    svg = base64.b64decode(match.group(1)).decode("utf-8", "ignore")
    width = re.search(r'width="(\d+)"', svg)
    height = re.search(r'height="(\d+)"', svg)

    if not width or not height:
        return 0

    return min(int(width.group(1)), int(height.group(1)))


def find_tables(page):
    # 표 안에 표가 있어도 짝이 맞게 [시작, 끝) 위치를 구한다
    spans = []
    stack = []

    for match in re.finditer(r"<table|</table>", page):
        if match.group(0) == "<table":
            stack.append(match.start())
        elif stack:
            spans.append((stack.pop(), match.end()))

    spans.sort()

    return spans


def find_images(fragment):
    # 지연 로딩 사진(아이콘, 로고 svg, 너무 작은 사진 제외)
    images = []

    for match in re.finditer(r"<img[^>]*data-src='([^']+)'[^>]*>", fragment):
        source = match.group(1)

        if source.endswith(".svg"):
            continue

        side = placeholder_side(match.group(0))

        if side < MIN_IMAGE_SIDE:
            continue

        alt_match = re.search(r"alt='([^']*)'", match.group(0))
        alt = html.unescape(alt_match.group(1)) if alt_match else ""
        images.append({"source": "https:" + source if source.startswith("//") else source, "alt": alt, "side": side})

    return images


def to_text(fragment):
    return re.sub(r"\s+", " ", html.unescape(re.sub(r"<[^>]+>", " ", fragment))).strip()


def score_image(text, core_title, alt):
    # 소개 상자일수록 높은 점수: 제목이 앞에 나옴, 항목 이름(본명, 종족 등)이 많음, 사진 설명에 제목과 프로필이 있음
    score = 0

    if core_title in text[:TITLE_WINDOW]:
        score += 3

    score += min(len(set(INFOBOX_LABELS.findall(text))), 8)

    # 유튜브 프로필은 채널 아이콘이라 대표 사진으로 부적절하다
    if "유튜브" in alt:
        return score

    if core_title in alt:
        score += 3

    if "프로필" in alt:
        score += 2

    return score


def find_english_name(text, core_title):
    # 소개 상자에서 제목 바로 뒤에 나오는 영문 이름 (예: 아야츠노 유니 Ayatsuno Yuni)
    index = text.find(core_title)

    if index < 0:
        return None

    start = index + len(core_title)
    match = ENGLISH_NAME.search(text[start:start + ENGLISH_WINDOW])

    return match.group(0) if match else None


def find_candidates(page, title):
    core_title = re.sub(r"\(.*?\)", "", title).strip()
    candidates = {}
    table_texts = []

    for start, end in find_tables(page):
        table = page[start:end]
        images = find_images(table)

        if not 1 <= len(images) <= MAX_TABLE_IMAGES:
            continue

        text = to_text(table)
        score = score_image(text, core_title, images[0]["alt"])
        table_texts.append((score, text))

        # 같은 표의 둘째 사진부터는 한 점씩 낮춘다
        for index, image in enumerate(images):
            image_score = score_image(text, core_title, image["alt"]) - index

            if image_score <= 0:
                continue

            add_candidate(candidates, image, image_score, "소개 상자 표")

    # 표 밖(소개 상자가 표가 아닌 문서)에서 사진 설명에 제목과 프로필이 같이 있는 사진
    for image in find_images(page):
        alt = image["alt"]

        if core_title in alt and "프로필" in alt and "유튜브" not in alt:
            add_candidate(candidates, image, PROFILE_ALT_SCORE, "프로필 사진")

    og = find_og_image(page)

    if og:
        add_candidate(candidates, {"source": og, "alt": "문서 대표 이미지", "side": 0}, 1, "문서 대표")

    # 점수가 같으면 큰 사진이 먼저 온다
    ordered = sorted(candidates.values(), key=lambda candidate: (-candidate["score"], -candidate["side"]))[:MAX_CANDIDATES]
    table_texts.sort(key=lambda entry: -entry[0])
    english_name = None

    for score, text in table_texts:
        english_name = find_english_name(text, core_title)

        if english_name:
            break

    return ordered, english_name


def add_candidate(candidates, image, score, origin):
    current = candidates.get(image["source"])

    if current and current["score"] >= score:
        return

    candidates[image["source"]] = {"source": image["source"], "alt": image["alt"], "side": image["side"], "score": score, "origin": origin}


def find_og_image(page):
    match = re.search(r'og:image" content="([^"]+)"', page)

    if not match:
        return None

    source = match.group(1)

    if source.endswith(".svg"):
        return None

    return "https:" + source if source.startswith("//") else source


def build_item(item_id, url):
    item = {"id": item_id, "url": url, "title": urllib.parse.unquote(url)[len(URL_PREFIX):], "name": "", "candidates": [],
            "confidence": "none", "error": None}

    try:
        page = fetch(url).decode("utf-8", "ignore")
        title = find_title(page)

        if not title:
            raise RuntimeError("문서 제목을 찾지 못했습니다")

        item["title"] = title
        candidates, english_name = find_candidates(page, title)
        item["candidates"] = candidates
        item["name"] = to_slug(english_name) if english_name else "item-{:03d}".format(item_id + 1)

        if candidates:
            item["confidence"] = "high" if candidates[0]["score"] >= HIGH_SCORE else "low"
    except Exception as error:
        item["error"] = str(error)

    return item


def sips_size(path):
    result = subprocess.run(["sips", "-g", "pixelWidth", "-g", "pixelHeight", str(path)],
                            capture_output=True, text=True, check=True)
    width = int(re.search(r"pixelWidth: (\d+)", result.stdout).group(1))
    height = int(re.search(r"pixelHeight: (\d+)", result.stdout).group(1))

    return width, height


def make_square_png(source_path, target_path):
    subprocess.run(["sips", "-s", "format", "png", str(source_path), "--out", str(target_path)],
                   capture_output=True, check=True)
    width, height = sips_size(target_path)

    # 가로가 길면 가운데, 세로가 길면 위쪽(얼굴) 기준으로 자른다
    if width > height:
        subprocess.run(["sips", "-c", str(height), str(height), str(target_path)], capture_output=True, check=True)
    elif height > width:
        subprocess.run(["sips", "-c", str(width), str(width), "--cropOffset", "0", "0", str(target_path)],
                       capture_output=True, check=True)

    side = min(width, height)

    if side > MAX_SIDE:
        subprocess.run(["sips", "-Z", str(MAX_SIDE), str(target_path)], capture_output=True, check=True)

    return side


def unique_path(name):
    path = OUTPUT_DIR / (name + ".png")
    number = 2

    while path.exists():
        path = OUTPUT_DIR / (name + "-" + str(number) + ".png")
        number += 1

    return path


class ReviewSession:
    """검토 화면이 쓰는 상태: 문서별 후보, 받아 둔 사진, 저장 결과"""

    def __init__(self, items, work_dir):
        self.items = items
        self.work_dir = work_dir
        self.image_cache = {}
        self.cache_lock = threading.Lock()
        self.download_slots = threading.Semaphore(IMAGE_DOWNLOAD_PARALLEL)
        self.saved = False
        self.save_lock = threading.Lock()

    def candidate_image(self, item_id, candidate_index):
        item = self.items[item_id]
        source = item["candidates"][candidate_index]["source"]

        with self.cache_lock:
            if source in self.image_cache:
                return source, self.image_cache[source]

        with self.download_slots:
            data = fetch(source, referer="https://namu.wiki/")

        with self.cache_lock:
            self.image_cache[source] = data

        return source, data

    def save(self, choices):
        # choices: [{"id": 문서 번호, "candidate": 후보 번호, "name": 파일 이름}] 제외한 문서는 보내지 않는다
        results = []
        OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

        for choice in choices:
            item = self.items[choice["id"]]

            try:
                source, data = self.candidate_image(choice["id"], choice["candidate"])
                source_path = self.work_dir / ("source-" + str(choice["id"]))
                source_path.write_bytes(data)
                name = to_slug(choice.get("name", "")) or "item-{:03d}".format(choice["id"] + 1)
                target_path = unique_path(name)
                side = make_square_png(source_path, target_path)
                results.append({"title": item["title"], "file": target_path.name, "side": side, "error": None})
            except Exception as error:
                results.append({"title": item["title"], "file": None, "side": 0, "error": str(error)})

        return results


def make_handler(session, server_holder):
    class Handler(BaseHTTPRequestHandler):

        def log_message(self, format, *args):
            pass

        def send_bytes(self, status, content_type, body):
            self.send_response(status)
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def send_json(self, status, value):
            self.send_bytes(status, "application/json; charset=utf-8", json.dumps(value, ensure_ascii=False).encode("utf-8"))

        def do_GET(self):
            path = urllib.parse.urlparse(self.path).path

            if path == "/":
                self.send_bytes(200, "text/html; charset=utf-8", REVIEW_PAGE.read_bytes())
            elif path == "/api/items":
                self.send_json(200, session.items)
            elif path.startswith("/img/"):
                self.send_image(path)
            else:
                self.send_bytes(404, "text/plain; charset=utf-8", b"not found")

        def send_image(self, path):
            try:
                _, _, item_id, candidate_index = path.split("/")
                source, data = session.candidate_image(int(item_id), int(candidate_index))
                extension = source.rsplit(".", 1)[-1].lower()
                self.send_bytes(200, CONTENT_TYPES.get(extension, "application/octet-stream"), data)
            except Exception:
                self.send_bytes(502, "text/plain; charset=utf-8", b"image fetch failed")

        def do_POST(self):
            if urllib.parse.urlparse(self.path).path != "/api/save":
                self.send_bytes(404, "text/plain; charset=utf-8", b"not found")
                return

            with session.save_lock:
                if session.saved:
                    self.send_json(409, {"error": "이미 저장했습니다"})
                    return

                length = int(self.headers.get("Content-Length", "0"))
                choices = json.loads(self.rfile.read(length).decode("utf-8"))
                results = session.save(choices)
                session.saved = True

            self.send_json(200, results)
            threading.Thread(target=server_holder["server"].shutdown).start()

    return Handler


def start_server(session):
    server_holder = {}
    handler = make_handler(session, server_holder)

    for port in range(PORT_START, PORT_START + PORT_TRIES):
        try:
            server = ThreadingHTTPServer(("127.0.0.1", port), handler)
        except OSError:
            continue

        server_holder["server"] = server

        return server, port

    raise RuntimeError("열 수 있는 포트가 없습니다 (" + str(PORT_START) + "~" + str(PORT_START + PORT_TRIES - 1) + ")")


def open_browser(url):
    result = subprocess.run(["open", "-a", "Google Chrome", url], capture_output=True)

    if result.returncode != 0:
        webbrowser.open(url)


def read_urls():
    urls = []

    print("나무위키 캐릭터 문서 주소를 한 줄에 하나씩 붙여넣고 엔터를 누르세요.")
    print("다 넣었으면 exit 를 입력하세요. 그때 검토 화면이 브라우저로 열립니다.")
    print("(취소하려면 Ctrl+C)")

    while True:
        try:
            line = input("URL> ").strip()
        except EOFError:
            break

        if not line:
            continue

        if line == "exit":
            break

        url = urllib.parse.unquote(line)

        if not url.startswith(URL_PREFIX):
            print("  나무위키 문서 주소(" + URL_PREFIX + "...)만 받습니다")
            continue

        if url in urls:
            print("  이미 입력한 주소입니다")
            continue

        urls.append(url)
        print("  " + str(len(urls)) + "개 입력됨")

    return urls


def main():
    urls = read_urls()

    if not urls:
        print("입력한 주소가 없습니다")
        return

    items = []

    for index, url in enumerate(urls):
        item = build_item(index, url)
        items.append(item)
        state = item["error"] if item["error"] else item["confidence"]
        print("[" + str(index + 1) + "/" + str(len(urls)) + "] " + item["title"] + " : " + state)
        time.sleep(REQUEST_INTERVAL_SECONDS)

    work_dir = Path(tempfile.mkdtemp())

    try:
        session = ReviewSession(items, work_dir)
        server, port = start_server(session)
        address = "http://127.0.0.1:" + str(port) + "/"
        print("\n검토 화면: " + address + " (저장하면 자동으로 종료됩니다)")
        open_browser(address)
        server.serve_forever()
        server.server_close()
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)

    if session.saved:
        print("저장했습니다 -> " + str(OUTPUT_DIR))
    else:
        print("저장하지 않고 종료했습니다")


if __name__ == "__main__":
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print("\n취소했습니다")
        sys.exit(1)
