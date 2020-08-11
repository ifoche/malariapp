/*
 * Copyright (c) 2015.
 *
 * This file is part of Health Network QIS App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.adapters.dashboard;

import static org.eyeseetea.malariacare.DashboardActivity.dashboardActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.domain.entity.CompetencyScoreClassification;
import org.eyeseetea.malariacare.domain.entity.ServerClassification;
import org.eyeseetea.malariacare.utils.CompetencyUtils;
import org.eyeseetea.malariacare.utils.DateParser;
import org.eyeseetea.malariacare.views.CustomTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentSentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public static final String SCORE_FORMAT = "%.1f %%";

    List<SurveyDB> surveys = new ArrayList<>();
    ServerClassification classification;

    public AssessmentSentAdapter(ServerClassification classification) {
        this.classification = classification;
    }

    public void setSurveys(List<SurveyDB> surveys){
        this.surveys = surveys;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.assessment_sent_record, viewGroup, false);
        return new AssessmentSentAdapter.ViewHolder(itemView, classification);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((AssessmentSentAdapter.ViewHolder) viewHolder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return surveys.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public static final String COMPLETED_SURVEY_MARK = "* ";

        private SurveyDB survey;
        private ImageView menuDots;
        private CustomTextView facilityName;
        private CustomTextView surveyType;
        private CustomTextView classificationTextView;
        private ServerClassification serverClassification;

        public ViewHolder(View itemView,
                ServerClassification serverClassification) {
            super(itemView);

            this.serverClassification = serverClassification;
            menuDots = itemView.findViewById(R.id.menu_dots);
            facilityName = itemView.findViewById(R.id.facility);
            surveyType = itemView.findViewById(R.id.survey_type);
            menuDots.setOnClickListener(view -> dashboardActivity.onFeedbackSelected(survey));
            classificationTextView = itemView.findViewById(R.id.score);
        }

        void bindView(int position) {
            survey = surveys.get(position);

            adjustRowPadding();

            renderSentDate(survey);
            renderSentScore(survey);
            renderFacility(facilityName, surveyType, survey);
            renderSurveyType(surveyType, survey);

            decorateBackground(position);
        }

        private void renderFacility(CustomTextView facilityName, CustomTextView surveyType,
                SurveyDB survey) {
            facilityName.setText(survey.getOrgUnit().getName());
        }

        private void renderSentScore(SurveyDB survey) {

            String competencyText;

            if (survey.hasConflict()) {
                competencyText = (itemView.getContext().getResources().getString(
                        R.string.feedback_info_conflict)).toUpperCase();
                classificationTextView.setText(competencyText);
            } else {
                if (serverClassification == ServerClassification.COMPETENCIES){
                    CompetencyScoreClassification classification =
                            CompetencyScoreClassification.get(
                                    survey.getCompetencyScoreClassification());

                    CompetencyUtils.setTextByCompetencyAbbreviation(classificationTextView, classification);
                } else {
                    if (survey.hasMainScore()){
                        String value = survey.getMainScore().getScore() + " %";
                        classificationTextView.setText(value);
                    } else {
                        classificationTextView.setText("-");
                    }
                }
            }
    }

        private void renderSentDate(SurveyDB survey) {
            CustomTextView sentDate = itemView.findViewById(R.id.sentDate);
            sentDate.setText(decorateCompletionDate(survey));
        }

        private String decorateCompletionDate(SurveyDB survey) {
            Date completionDate = survey.getCompletionDate();
            DateParser dateParser = new DateParser();
            return dateParser.getEuropeanFormattedDate(completionDate);
        }

        private CustomTextView renderSurveyType(CustomTextView surveyType, SurveyDB survey) {
            String surveyDescription;
            if (survey.isCompleted()) {
                surveyDescription = COMPLETED_SURVEY_MARK + survey.getProgram().getName();
            } else {
                surveyDescription = survey.getProgram().getName();
            }
            surveyType.setText(surveyDescription);
            return surveyType;
        }

        private void decorateBackground(int position) {
            if (position == 0 || position % 2 == 0) {
                itemView.setBackgroundColor(
                        itemView.getContext().getResources().getColor(R.color.white));
            } else {
                itemView.setBackgroundColor(
                        itemView.getContext().getResources().getColor(R.color.white_grey));
            }
        }

        protected void adjustRowPadding() {
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            int paddingDp = (int) (5 * density);

            itemView.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        }
    }
}