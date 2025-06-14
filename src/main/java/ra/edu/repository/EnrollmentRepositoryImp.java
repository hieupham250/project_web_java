package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Enrollment;

@Repository
public class EnrollmentRepositoryImp implements EnrollmentRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean checkEnrollment(int userId, int courseId) {
        Session session = sessionFactory.openSession();
        Query<Long> query = session.createQuery("SELECT COUNT(e.id) FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("courseId", courseId);
        return query.uniqueResult() > 0;
    }

    @Override
    public void create(Enrollment enrollment) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(enrollment);
        session.getTransaction().commit();
    }
}
