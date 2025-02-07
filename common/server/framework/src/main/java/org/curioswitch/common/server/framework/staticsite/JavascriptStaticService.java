/*
 * MIT License
 *
 * Copyright (c) 2019 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.curioswitch.common.server.framework.staticsite;

import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.ServerCacheControl;
import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import javax.inject.Inject;

public class JavascriptStaticService extends AbstractHttpService {

  private final HttpHeaders INFINITE_CACHE_HEADERS =
      HttpHeaders.of(
          HttpHeaderNames.CACHE_CONTROL,
          ServerCacheControl.IMMUTABLE.asHeaderValue(),
          HttpHeaderNames.VARY,
          "Accept-Encoding");

  private final HttpData response;

  @Inject
  public JavascriptStaticService(Config globalConfig) {
    String rendered =
        globalConfig.getObject("javascriptConfig").render(ConfigRenderOptions.concise());
    response = HttpData.ofUtf8("var CONFIG = " + rendered + ";");
  }

  @Override
  protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
    ctx.addAdditionalResponseHeaders(INFINITE_CACHE_HEADERS);
    return HttpResponse.of(HttpStatus.OK, MediaType.JAVASCRIPT_UTF_8, response);
  }
}
