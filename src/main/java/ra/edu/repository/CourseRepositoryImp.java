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
        StringBuilder hql = new StringBuilder("FROM Course c WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) hql.append(" AND c.name LIKE :keyword");

        if ("desc".equalsIgnoreCase(sortDirection)) {
            hql.append(" ORDER BY c.name DESC");
        } else {
            hql.append(" ORDER BY c.name ASC");
        }

        Query<Course> query = session.createQuery(hql.toString(), Course.class);
        if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");

        return query.setFirstResult((page - 1) * size).setMaxResults(size).list();
    }

    @Override
    public long countWithFilter(String keyword) {
        Session session = sessionFactory.openSession();
        StringBuilder hql = new StringBuilder("SELECT COUNT(c.id) FROM Course c WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) hql.append(" AND c.name LIKE :keyword");

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");

        return query.uniqueResult();
    }

    @Override
    public Course findById(int id) {
        Session session = sessionFactory.openSession();
        return session.get(Course.class, id);
    }

    @Override
    public List<Course> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        Session session = sessionFactory.openSession();
        Query<Course> query = session.createQuery("FROM Course c WHERE c.id in (:ids)", Course.class);
        query.setParameter("ids", ids);
        return query.list();
    }

    @Override
    public boolean existsByName(String name) {
        Session session = sessionFactory.openSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(c.id) FROM Course c WHERE lower(c.name) = :name", Long.class);
        query.setParameter("name", name.toLowerCase());

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public void create(Course course) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(course);
        session.getTransaction().commit();
    }

    @Override
    public void update(Course course) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(course);
        session.getTransaction().commit();
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Course course = session.get(Course.class, id);
        if(course == null) return;
        session.delete(course);
        session.getTransaction().commit();
    }
}
