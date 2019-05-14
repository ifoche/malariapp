package org.eyeseetea.malariacare.presentation.viewmodels.observation;

public class QuestionViewModel extends MissedStepViewModel {
    public QuestionViewModel(long questionId, long compositeScoreParentId, String name) {
        super(QUESTION_KEY_PREFIX + questionId,
                COMPOSITE_KEY_PREFIX + compositeScoreParentId,
                name, false);
    }
}
