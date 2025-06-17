package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.datatype.Role;
import ra.edu.entity.Course;

import java.util.List;

@Repository
public class DashboardRepositoryImp implements DashboardRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long countTotalCourses() {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(c.id) FROM Course c", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    @Override
    public long countTotalStudents() {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(s.id) FROM User s WHERE s.role = :role", Long.class);
            query.setParameter("role", Role.STUDENT);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    @Override
    public long countTotalEnrollments() {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(e.id) FROM Enrollment e", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Course> getAllCourses() {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.enrollments", Course.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Course> get5bestCourses() {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.enrollments ORDER BY size(c.enrollments) DESC", Course.class);
            query.setMaxResults(5);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            session.close();
        }
    }
}
