package com.smartconsultor.microservice.business.di.component;

import com.smartconsultor.microservice.business.FlinkJobRunner;
import com.smartconsultor.microservice.business.adapter.flink.process.TransactionProcessFunction;
import com.smartconsultor.microservice.business.application.usecase.DepositUseCase;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    com.smartconsultor.microservice.business.di.module.ApplicationModule.class,
    //com.smartconsultor.microservice.business.di.module.PulsarModule.class,
    //com.smartconsultor.microservice.business.di.module.RepositoryModule.class,
    com.smartconsultor.microservice.business.di.module.FlinkModule.class
})
public interface FlinkJobComponent {
    // Inject các lớp cần thiết ở đây
    void inject(TransactionProcessFunction processFunction);
    void inject(FlinkJobRunner jobRunner);

    DepositUseCase getDepositUseCase();
}
