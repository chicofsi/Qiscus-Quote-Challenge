/*
 * Copyright (c) 2016 Qiscus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chico.qiscuschallenge.ViewModel;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.subjects.BehaviorSubject;

public abstract class QiscusVM<V extends ChatVM.View> {
    private final BehaviorSubject<QiscusEvent> lifecycleSubject = BehaviorSubject.create();

    protected V view;

    public QiscusVM(V view) {
        this.view = view;
        lifecycleSubject.onNext(QiscusEvent.CREATE);
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, QiscusEvent.DETACH);
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(QiscusEvent qiscusPresenterEvent) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, qiscusPresenterEvent);
    }

    public void detachView() {
        view = null;
        lifecycleSubject.onNext(QiscusEvent.DETACH);
    }

    public interface View {
        void showError(String errorMessage);

        void showLoading();

        void dismissLoading();
    }
}
