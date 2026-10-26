package com.jong.mikumikureservationbackend.controller.figure;

import com.jong.mikumikureservationbackend.common.response.ApiResponse;
import com.jong.mikumikureservationbackend.common.security.CurrentUser;
import com.jong.mikumikureservationbackend.controller.figure.doc.FigureFormControllerDocs;
import com.jong.mikumikureservationbackend.service.figure.FigureFormService;
import com.jong.mikumikureservationbackend.vo.figure.response.FigureFormOptionsResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/figure/form-options")
@RequiredArgsConstructor
public class FigureFormController implements FigureFormControllerDocs {

    private final FigureFormService figureFormService;
    private final CurrentUser currentUser;

    @Override
    @GetMapping
    public ApiResponse<FigureFormOptionsResponseVO> getOptions() {
        return ApiResponse.ok("조회가 완료되었습니다.", figureFormService.getOptions(currentUser.getId()));
    }
}
