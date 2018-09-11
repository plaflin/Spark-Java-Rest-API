package com.teamtreehouse.courses.dao;

import com.teamtreehouse.courses.exc.DaoException;
import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oReviewDaoTest {

    private Connection conn;
    private Sql2oCourseDao courseDao;
    private Sql2oReviewDao reviewDao;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "","");
        courseDao = new Sql2oCourseDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        // Keep connection open through entire test so that it isn't wiped out
        conn = sql2o.open();
        course = new Course("test", "http://testing.com");
        courseDao.add(course);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingReviewSetsNewId() throws Exception {
        Review review = new Review(course.getId(), 5, "Best Class Ever!");
        int originalID = review.getId();

        reviewDao.add(review);

        assertNotEquals(originalID, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistForACourse() {
        Review review1 = new Review(course.getId(), 1, "test");
        Review review2 = new Review(course.getId(), 2, "test");

        try {
            reviewDao.add(review1);
            reviewDao.add(review2);
        } catch (DaoException exc) {

        }

        List<Review> reviews = reviewDao.findByCourseId(course.getId());

        assertEquals(2, reviews.size());
    }

    @Test(expected = DaoException.class)
    public void addingAReviewToANonExistingCourseFails() throws Exception {
        Review review = new Review(42, 1, "test");

        reviewDao.add(review);
    }
}
