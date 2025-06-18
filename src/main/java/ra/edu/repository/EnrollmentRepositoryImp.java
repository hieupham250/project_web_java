package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.datatype.StatusEnrollment;
import ra.edu.entity.Enrollment;

import java.util.List;

@Repository
public class EnrollmentRepositoryImp implements EnrollmentRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Enrollment> findAll(String keyword, StatusEnrollment status, int userId, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM Enrollment e WHERE 1=1");
            // Nếu có userId (Student) thì lọc theo userId
            if (userId != 0) {
                hql.append(" AND e.user.id = :userId");
            }
            // Nếu có keyword thì tìm theo tên course
            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND e.course.name LIKE :keyword");
            }

            if (status != null) {
                hql.append(" AND e.status = :status");
            }

            Query<Enrollment> query = session.createQuery(hql.toString(), Enrollment.class);
            if (userId != 0) {
                query.setParameter("userId", userId);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }
            if (status != null) {
                query.setParameter("status", status);
            }

            return query.setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            session.close();
        }
    }

    @Override
    public long countWithFilter(String keyword, StatusEnrollment status, int userId) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("SELECT COUNT(e.id) FROM Enrollment e WHERE 1=1");
            if (userId != 0) {
                hql.append(" AND e.user.id = :userId");
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND e.course.name LIKE :keyword");
            }
            if (status != null) {
                hql.append(" AND e.status = :status");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (userId != 0) {
                query.setParameter("userId", userId);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword + "%");
            }

            if (status != null) {
                query.setParameter("status", status);
            }

            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean checkEnrollment(int userId, int courseId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(e.id) FROM Enrollment e WHERE e.user.id = :userId AND e.course.id = :courseId", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("courseId", courseId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Integer> findAllCourseIdsByUserId(int userId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT e.course.id FROM Enrollment e WHERE e.user.id = :userId";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean create(Enrollment enrollment) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(enrollment);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
