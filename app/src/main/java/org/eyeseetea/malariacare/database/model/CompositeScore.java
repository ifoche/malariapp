/*
 * Copyright (c) 2015.
 *
 * This file is part of QA App.
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

package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.IConvertToSDKVisitor;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.VisitableToSDK;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(databaseName = AppDatabase.NAME)
public class CompositeScore extends BaseModel implements VisitableToSDK {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_composite_score;
    @Column
    String hierarchical_code;
    @Column
    String label;
    @Column
    String uid;
    @Column
    Integer order_pos;
    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_parent",
            columnType = Long.class,
            foreignColumnName = "id_composite_score")},
            saveForeignKeyModel = false)
    CompositeScore compositeScore;

    List<CompositeScore> compositeScoreChildren;

    List<Question> questions;

    public CompositeScore() {
    }

    public CompositeScore(String hierarchical_code, String label, CompositeScore compositeScore, Integer order_pos) {
        this.hierarchical_code = hierarchical_code;
        this.label = label;
        this.compositeScore = compositeScore;
        this.order_pos = order_pos;
    }

    public CompositeScore(String hierarchical_code, String label, String uid, CompositeScore compositeScore, Integer order_pos) {
        this.hierarchical_code = hierarchical_code;
        this.label = label;
        this.uid = uid;
        this.compositeScore = compositeScore;
        this.order_pos = order_pos;
    }

    public Long getId_composite_score() {
        return id_composite_score;
    }

    public void setId_composite_score(Long id_composite_score) {
        this.id_composite_score = id_composite_score;
    }

    public String getHierarchical_code() { return hierarchical_code; }

    public void setHierarchical_code(String hierarchical_code) { this.hierarchical_code = hierarchical_code; }
    public String getCode() { return hierarchical_code; }

    public void setCode(String code) { this.hierarchical_code = code; }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CompositeScore getComposite_score() {
        return compositeScore;
    }

    public void setCompositeScore(CompositeScore compositeScore) {
        this.compositeScore = compositeScore;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public boolean hasParent(){
        return getComposite_score() != null;
    }

    //TODO: to enable lazy loading, here we need to set Method.SAVE and Method.DELETE and use the .toModel() to specify when do we want to load the models
    public List<CompositeScore> getCompositeScoreChildren() {
        if (this.compositeScoreChildren == null){
            this.compositeScoreChildren = new Select()
                    .from(CompositeScore.class)
                    .where(Condition.column(CompositeScore$Table.COMPOSITESCORE_ID_PARENT).eq(this.getId_composite_score()))
                    .orderBy(CompositeScore$Table.ORDER_POS)
                    .queryList();
        }
        return this.compositeScoreChildren;
    }

    //TODO: to enable lazy loading, here we need to set Method.SAVE and Method.DELETE and use the .toModel() to specify when do we want to load the models
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "questions")
    public List<Question> getQuestions(){
        questions = new Select()
                .from(Question.class)
                .where(Condition.column(Question$Table.COMPOSITESCORE_ID_COMPOSITE_SCORE).eq(this.getId_composite_score()))
                .orderBy(true, Question$Table.ORDER_POS)
                .queryList();
        return questions;
    }

    //TODO: to enable lazy loading, here we need to set Method.SAVE and Method.DELETE and use the .toModel() to specify when do we want to load the models
    public static List<CompositeScore> listParentCompositeScores(CompositeScore compositeScore){
        ArrayList<CompositeScore> parentScores= new ArrayList<>();
        if(compositeScore==null || !compositeScore.hasParent()){
            return parentScores;
        }
        CompositeScore currentScore=compositeScore;
        while(currentScore!=null && currentScore.hasParent()){
            currentScore=currentScore.getComposite_score();
            parentScores.add(currentScore);
        }
        return parentScores;
    }
    /**
     * Select all composite score that belongs to a program
     * @param program Program whose composite scores are searched.
     * @return
     */
    public static List<CompositeScore> listAllByProgram(Program program){
        if(program==null || program.getId_program()==null){
            return new ArrayList<>();
        }
        //FIXME: Apparently there is a bug in DBFlow joins that affects here. Question has a column 'uid', and so do CompositeScore, so results are having Questions one, and should keep CompositeScore one. To solve it, we've introduced a last join with CompositeScore again and a HashSet to remove resulting duplicates
        //Take scores associated to questions of the program ('leaves')
        List<CompositeScore> compositeScoresByProgram = new Select().distinct().from(CompositeScore.class).as("cs")
                .join(Question.class, Join.JoinType.LEFT).as("q")
                .on(Condition.column(ColumnAlias.columnWithTable("cs", CompositeScore$Table.ID_COMPOSITE_SCORE))
                        .eq(ColumnAlias.columnWithTable("q", Question$Table.COMPOSITESCORE_ID_COMPOSITE_SCORE)))
                .join(Header.class, Join.JoinType.LEFT).as("h")
                .on(Condition.column(ColumnAlias.columnWithTable("q", Question$Table.HEADER_ID_HEADER))
                        .eq(ColumnAlias.columnWithTable("h", Header$Table.ID_HEADER)))
                .join(Tab.class, Join.JoinType.LEFT).as("t")
                .on(Condition.column(ColumnAlias.columnWithTable("h", Header$Table.TAB_ID_TAB))
                        .eq(ColumnAlias.columnWithTable("t", Tab$Table.ID_TAB)))
                .join(Program.class, Join.JoinType.LEFT).as("p")
                .on(Condition.column(ColumnAlias.columnWithTable("t", Tab$Table.PROGRAM_ID_PROGRAM))
                        .eq(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM)))
                .join(CompositeScore.class, Join.JoinType.LEFT).as("cs2")
                .on(Condition.column(ColumnAlias.columnWithTable("cs", CompositeScore$Table.ID_COMPOSITE_SCORE))
                        .eq(ColumnAlias.columnWithTable("cs2", CompositeScore$Table.ID_COMPOSITE_SCORE)))
                .where(Condition.column(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM))
                        .eq(program.getId_program()))
                .queryList();

        // remove duplicates
        Set<CompositeScore> uniqueCompositeScoresByProgram = new HashSet<>();
        uniqueCompositeScoresByProgram.addAll(compositeScoresByProgram);
        compositeScoresByProgram.clear();
        compositeScoresByProgram.addAll(uniqueCompositeScoresByProgram);

        //Find parent scores from 'leaves'
        Set<CompositeScore> parentCompositeScores = new HashSet<>();
        for(CompositeScore compositeScore: compositeScoresByProgram){
            parentCompositeScores.addAll(listParentCompositeScores(compositeScore));
        }
        compositeScoresByProgram.addAll(parentCompositeScores);

        //return all scores
        return compositeScoresByProgram;
    }

    /**
     * Select all composite score that belongs to a program
     * @param tabGroup Program whose composite scores are searched.
     * @return
     */
    //TODO: to enable lazy loading, here we need to set Method.SAVE and Method.DELETE and use the .toModel() to specify when do we want to load the models
    public static List<CompositeScore> listByTabGroup(TabGroup tabGroup){
        if(tabGroup==null || tabGroup.getId_tab_group()==null){
            return new ArrayList<>();
        }

        //FIXME: Apparently there is a bug in DBFlow joins that affects here. Question has a column 'uid', and so do CompositeScore, so results are having Questions one, and should keep CompositeScore one. To solve it, we've introduced a last join with CompositeScore again and a HashSet to remove resulting duplicates
        //Take scores associated to questions of the program ('leaves')
        List<CompositeScore> compositeScoresByProgram = new Select().distinct().from(CompositeScore.class).as("cs")
                .join(Question.class, Join.JoinType.LEFT).as("q")
                .on(Condition.column(ColumnAlias.columnWithTable("cs", CompositeScore$Table.ID_COMPOSITE_SCORE))
                        .eq(ColumnAlias.columnWithTable("q", Question$Table.COMPOSITESCORE_ID_COMPOSITE_SCORE)))
                .join(Header.class, Join.JoinType.LEFT).as("h")
                .on(Condition.column(ColumnAlias.columnWithTable("q", Question$Table.HEADER_ID_HEADER))
                        .eq(ColumnAlias.columnWithTable("h", Header$Table.ID_HEADER)))
                .join(Tab.class, Join.JoinType.LEFT).as("t")
                .on(Condition.column(ColumnAlias.columnWithTable("h", Header$Table.TAB_ID_TAB))
                        .eq(ColumnAlias.columnWithTable("t", Tab$Table.ID_TAB)))
                .join(TabGroup.class, Join.JoinType.LEFT).as("g")
                .on(Condition.column(ColumnAlias.columnWithTable("t", Tab$Table.TABGROUP_ID_TAB_GROUP))
                        .eq(ColumnAlias.columnWithTable("g", TabGroup$Table.ID_TAB_GROUP)))
                .join(CompositeScore.class, Join.JoinType.LEFT).as("cs2")
                .on(Condition.column(ColumnAlias.columnWithTable("cs", CompositeScore$Table.ID_COMPOSITE_SCORE))
                        .eq(ColumnAlias.columnWithTable("cs2", CompositeScore$Table.ID_COMPOSITE_SCORE)))
                .where(Condition.column(ColumnAlias.columnWithTable("g", TabGroup$Table.ID_TAB_GROUP))
                        .eq(tabGroup.getId_tab_group()))
                .orderBy(true, CompositeScore$Table.ORDER_POS)
                .queryList();


        // remove duplicates
        Set<CompositeScore> uniqueCompositeScoresByProgram = new HashSet<>();
        uniqueCompositeScoresByProgram.addAll(compositeScoresByProgram);
        compositeScoresByProgram.clear();
        compositeScoresByProgram.addAll(uniqueCompositeScoresByProgram);

        //Find parent scores from 'leaves'
        Set<CompositeScore> parentCompositeScores = new HashSet<>();
        for(CompositeScore compositeScore: compositeScoresByProgram){
            parentCompositeScores.addAll(listParentCompositeScores(compositeScore));
        }
        compositeScoresByProgram.addAll(parentCompositeScores);


        Collections.sort(compositeScoresByProgram, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {

                CompositeScore cs1 = (CompositeScore) o1;
                CompositeScore cs2 = (CompositeScore) o2;

                return new Integer(cs1.getOrder_pos().compareTo(new Integer(cs2.getOrder_pos())));
            }
        });



        //return all scores
        return compositeScoresByProgram;
    }
    public boolean hasChildren(){
        return !getCompositeScoreChildren().isEmpty();
    }

    @Override
    public void accept(IConvertToSDKVisitor IConvertToSDKVisitor) {
        IConvertToSDKVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeScore)) return false;

        CompositeScore that = (CompositeScore) o;

        if (id_composite_score != that.id_composite_score) return false;
        if (!hierarchical_code.equals(that.hierarchical_code)) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (order_pos != null ? !order_pos.equals(that.order_pos) : that.order_pos != null) return false;
        return !(compositeScore != null ? !compositeScore.equals(that.compositeScore) : that.compositeScore != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_composite_score ^ (id_composite_score >>> 32));
        result = 31 * result + hierarchical_code.hashCode();
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (compositeScore != null ? compositeScore.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompositeScore{" +
                "id=" + id_composite_score +
                ", code='" + hierarchical_code + '\'' +
                ", label='" + label + '\'' +
                ", uid='" + uid + '\'' +
                ", order_pos='" + order_pos + '\'' +
                ", compositeScore=" + compositeScore +
                '}';
    }
}
