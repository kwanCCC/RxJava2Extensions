/*
 * Copyright 2016-2018 David Karnok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.akarnokd.rxjava2.debug;

import java.util.concurrent.Callable;

import hu.akarnokd.rxjava2.debug.SingleOnAssembly.OnAssemblySingleObserver;
import io.reactivex.*;
import io.reactivex.exceptions.Exceptions;

/**
 * Wraps a Publisher and inject the assembly info.
 *
 * @param <T> the value type
 */
final class SingleOnAssemblyCallable<T> extends Single<T> implements Callable<T> {

    final SingleSource<T> source;

    final RxJavaAssemblyException assembled;

    SingleOnAssemblyCallable(SingleSource<T> source) {
        this.source = source;
        this.assembled = new RxJavaAssemblyException();
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> s) {
        source.subscribe(new OnAssemblySingleObserver<T>(s, assembled));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T call() throws Exception {
        try {
            return ((Callable<T>)source).call();
        } catch (Exception ex) {
            Exceptions.throwIfFatal(ex);
            throw (Exception)assembled.appendLast(ex);
        }
    }
}
