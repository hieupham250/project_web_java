package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.datatype.Role;
import ra.edu.datatype.StatusAccount;
import ra.edu.entity.User;

import java.util.List;

@Repository
public class UserRepositoryImp implements UserRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<User> findAll(String keyword, String sortDirection, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM User u WHERE u.role = :role");

            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND u.name LIKE :keyword OR u.email LIKE :keyword");
            }

            if ("asc".equalsIgnoreCase(sortDirection)) {
                hql.append(" ORDER BY u.name ASC");
            } else if ("desc".equalsIgnoreCase(sortDirection)) {
                hql.append(" ORDER BY u.name DESC");
            }

            Query<User> query = session.createQuery(hql.toString(), User.class);
            query.setParameter("role", Role.STUDENT);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
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
    public long countWithFilter(String keyword) {
        Session session = sessionFactory.openSession();

        try {
            StringBuilder hql = new StringBuilder("SELECT COUNT(u.id) FROM User u WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                hql.append(" AND u.name LIKE :keyword");
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
    public boolean updateStatus(int id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, id);
            if (user == null) return false;
            if (user.getStatus() == StatusAccount.ACTIVE) {
                user.setStatus(StatusAccount.INACTIVE);
            } else {
                user.setStatus(StatusAccount.ACTIVE);
            }
            session.update(user);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
}
