package apptive.fin.calculator;

import apptive.fin.calculator.controller.CalculatorController;
import apptive.fin.calculator.service.CalculatorService;
import apptive.fin.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class CalculatorControllerTest {

    private final CalculatorService calculatorService = mock(CalculatorService.class);
    private final MockMvc mockMvc = standaloneSetup(new CalculatorController(calculatorService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void enum에_없는_문자열이면_잘못된_입력으로_응답한다() throws Exception {
        mockMvc.perform(post("/calculator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productPropertyId": 1,
                                  "productType": "UNKNOWN_TYPE",
                                  "interestRateType": "SINGLE_INTEREST",
                                  "reserveType": null,
                                  "appliedRate": 0.04,
                                  "amount": 10000,
                                  "saveTrm": 12,
                                  "taxType": "GENERAL"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C003"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 입력입니다."));

        verifyNoInteractions(calculatorService);
    }
}
