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

package org.curioswitch.gradle.helpers.exec;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import org.curioswitch.gradle.helpers.exec.ExecWorkAction.Parameters;
import org.gradle.api.provider.Property;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

/** A {@link WorkAction} to execute an external process with the given customizer. */
public abstract class ExecWorkAction implements WorkAction<Parameters> {

  private final ExecOperations exec;

  @SuppressWarnings("InjectOnConstructorOfAbstractClass")
  @Inject
  public ExecWorkAction(ExecOperations exec) {
    this.exec = checkNotNull(exec, "exec");
  }

  @Override
  public void execute() {
    exec.exec(exec -> getParameters().getExecCustomizer().get().copyTo(exec));
  }

  public interface Parameters extends WorkParameters {
    Property<ExecCustomizer> getExecCustomizer();
  }
}
