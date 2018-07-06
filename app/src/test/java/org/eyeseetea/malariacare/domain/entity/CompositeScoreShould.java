package org.eyeseetea.malariacare.domain.entity;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;

public class CompositeScoreShould {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void create_composite_score_with_mandatory_fields() {
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);

        Assert.assertNotNull(compositeScore);
        Assert.assertTrue(compositeScore.getUid().equals("UID"));
        Assert.assertTrue(compositeScore.getLabel().equals("LABEL"));
        Assert.assertTrue(compositeScore.getHierarchicalCode().equals("HIERARCHICAL_CODE"));
        Assert.assertTrue(compositeScore.getOrderPos() == 1);
    }

    @Test
    public void create_composite_score_and_add_parent() {
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore parent = new CompositeScore("PARENT_UID", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addParent(parent);
        Assert.assertTrue(compositeScore.getParent().equals(parent));
    }

    @Test
    public void create_composite_score_and_add_child() {
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child = new CompositeScore("CHILD_UID", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addChild(child);
        Assert.assertTrue(compositeScore.getChildren().get(0).equals(child));
    }

    @Test
    public void create_composite_score_and_add_children() {
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child1 = new CompositeScore("CHILD_UID1", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child2 = new CompositeScore("CHILD_UID2", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child3 = new CompositeScore("CHILD_UID3", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addChildren(
                new ArrayList<>(Arrays.asList(child1, child2, child3)));
        Assert.assertTrue(compositeScore.getChildren().get(0).equals(child1));
        Assert.assertTrue(compositeScore.getChildren().get(1).equals(child2));
        Assert.assertTrue(compositeScore.getChildren().get(2).equals(child3));
    }

    @Test
    public void throw_exception_adding_empty_parent() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ParentUid is required and not empty");
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addParent(null);
    }

    @Test
    public void throw_exception_adding_empty_child() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ChildUid is required an not empty");
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addChild(null);
    }

    @Test
    public void throw_exception_adding_empty_children() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ChildUid is required an not empty");
        CompositeScore compositeScore = new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child1 = new CompositeScore("CHILD_UID1", "LABEL", "HIERARCHICAL_CODE", 1);
        CompositeScore child3 = new CompositeScore("CHILD_UID3", "LABEL", "HIERARCHICAL_CODE", 1);
        compositeScore.addChildren(new ArrayList<>(Arrays.asList(child1, null, child3)));
    }

    @Test
    public void throw_exception_when_create_composite_with_null_uid(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Uid is required");

        new CompositeScore(null, "LABEL", "HIERARCHICAL_CODE", 1);
    }

    @Test
    public void throw_exception_when_create_composite_with_null_label(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Label is required");

        new CompositeScore("UID", null, "HIERARCHICAL_CODE", 1);
    }

    @Test
    public void throw_exception_when_create_composite_with_null_hierarchicalCode(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("HierarchicalCode is required");

        new CompositeScore("UID", "LABEL", null, 1);
    }

    @Test
    public void throw_exception_when_create_composite_with_lower_than_0_orderPos(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("OrderPos has to be higher than 0");

        new CompositeScore("UID", "LABEL", "HIERARCHICAL_CODE", -1);
    }





}
