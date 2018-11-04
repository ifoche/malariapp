package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.boundaries.ISurveyDataSource;
import org.eyeseetea.malariacare.data.database.MetadataValidator;
import org.eyeseetea.malariacare.data.database.datasources.CompositeScoreDataSource;
import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.iomodules.dhis.exporter.PushDataController;
import org.eyeseetea.malariacare.data.database.iomodules.dhis.importer.PullDataController;
import org.eyeseetea.malariacare.data.database.iomodules.dhis.importer.PullMetadataController;
import org.eyeseetea.malariacare.data.network.ConnectivityManager;
import org.eyeseetea.malariacare.data.remote.sdk.data.SurveySDKDhisDataSource;
import org.eyeseetea.malariacare.data.repositories.ICompositeScoreRepository;
import org.eyeseetea.malariacare.data.repositories.OptionRepository;
import org.eyeseetea.malariacare.data.repositories.ServerMetadataRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOptionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IServerMetadataRepository;
import org.eyeseetea.malariacare.domain.usecase.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class SyncFactory {
    IAsyncExecutor asyncExecutor = new AsyncExecutor();
    IMainExecutor mainExecutor = new UIThreadExecutor();

    public PullUseCase getPullUseCase(Context context){
        PullMetadataController pullMetadataController = new PullMetadataController();


        IServerMetadataRepository serverMetadataRepository =
                new ServerMetadataRepository(context);
        IOptionRepository optionRepository = new OptionRepository();
        IQuestionRepository questionRepository = new QuestionLocalDataSource();
        ICompositeScoreRepository compositeScoreRepository = new CompositeScoreDataSource();
        IConnectivityManager connectivityManager = new ConnectivityManager();

        ISurveyDataSource surveyRemoteDataSource =
                new SurveySDKDhisDataSource(serverMetadataRepository,
                        questionRepository, optionRepository, compositeScoreRepository, connectivityManager);

        ISurveyDataSource surveyLocalDataSource = new SurveyLocalDataSource();

        PullDataController pullDataController =
                new PullDataController(surveyLocalDataSource, surveyRemoteDataSource);

        MetadataValidator metadataValidator = new MetadataValidator();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();

        PullUseCase pullUseCase = new PullUseCase(
                asyncExecutor, mainExecutor, pullMetadataController,
                pullDataController, metadataValidator);

        return pullUseCase;
    }

    public PushUseCase getPushUseCase(Context context){
        IConnectivityManager connectivityManager = new ConnectivityManager();
        IPushController pushController = new PushDataController(context, connectivityManager);
        PushUseCase pushUseCase = new PushUseCase(asyncExecutor, mainExecutor, pushController);

        return pushUseCase;
    }
}