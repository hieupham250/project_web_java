package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Course;

import java.util.List;

@Repository
public class CourseRepositoryImp implements CourseRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Course> findAll(String keyword, String sortDirection, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM Course c WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND c.name LIKE :keyword");
            }

            if ("asc".equalsIgnoreCase(sortDirection)) {
                hql.append(" ORDER BY c.name ASC");
            } else if ("desc".equalsIgnoreCase(sortDirection)) {
                hql.append(" ORDER BY c.name DESC");
            }

            Query<Course> query = session.createQuery(hql.toString(), Course.class);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }

            return query.setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // trả về danh sách rỗng nếu lỗi
        } finally {
            session.close();
        }
    }

    @Override
    public long countWithFilter(String keyword) {
        Session session = sessionFactory.openSession();

        try {
            StringBuilder hql = new StringBuilder("SELECT COUNT(c.id) FROM Course c WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND c.name LIKE :keyword");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
            }
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
    }

    @Override
    public Course findById(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Course.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByName(String name) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(c.id) FROM Course c WHERE lower(c.name) = :name", Long.class);
            query.setParameter("name", name.toLowerCase());

            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean create(Course course) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            session.save(course);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean update(Course course) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            session.update(course);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean checkHasStudents(int courseId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.id = :courseId", Long.class);
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
    public boolean delete(int id) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            Course course = session.get(Course.class, id);
            if (course == null) return false;
            session.delete(course);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
