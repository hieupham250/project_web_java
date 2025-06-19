package ra.edu.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.User;

@Repository
public class AuthRepositoryImp implements AuthRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean existsByUsername(String username) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(u.id) FROM User u WHERE u.username = :username", Long.class);
            query.setParameter("username", username);
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
    public boolean existsByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(u.id) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
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
    public boolean existsByPhone(String phone) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(u.id) FROM User u WHERE u.phone = :phone", Long.class);
            query.setParameter("phone", phone);
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
    public boolean register(User user) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(user);
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
    public User login(String username, String password) {
        Session session = sessionFactory.openSession();
        try {
            Query<User> query = session.createQuery("FROM User WHERE username =:username AND password = :password", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }
}
