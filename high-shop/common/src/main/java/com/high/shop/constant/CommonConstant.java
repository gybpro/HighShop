package com.high.shop.constant;

import java.time.format.DateTimeFormatter;

public interface CommonConstant {

    String AUTHORIZATION_PREFIX = "Authorization";

    String COMMON_JWT_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbGwiXSwiZXhwIjozODMwMDA0ODkzLCJqdGkiOiIyN2VmODExNS0yNmVlLTQ0NmQtYTVmNC1iNDBjMjdlZmQwNGMiLCJjbGllbnRfaWQiOiJhcHAifQ.dLNice9n96NCuxjcHM9DOlg3JE5vzWChyQZvbaKnVlDrJU_jQzG9krE021j9o1dtVm3z9oE86J8H9sI4oVSJj-pjbA-dCi5SUeP_9D08ZTGEW_IWwX6Ue35LMv1ZLfHzx1vdRFbhEcNPiEEpdbiWKSE0T-EwiP9QXKiPCCSZ9atnn-RoxGkcrkMcVoMPTFNUX3QFr4f3qnMOXC7cRSML-LMLSgwcwX8ls1q056An15aosBqVXrtSomZfn6uVGa-LLqtP-HZv42_4d_dxpGGn-arhfpNpjNhy_H5ByiFWu88lIpwIpSJUgLuPYyOE7rJgKL9RvuL_ydyi2N0aUZjCrA";

    Long DEFAULT_SHOP = 1L;
    Long DEFAULT_COUNT = 0L;

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
}
