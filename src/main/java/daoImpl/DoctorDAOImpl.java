package daoImpl;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Query;

import dao.IDoctorDAO;
import entity.Optional;
import exceptions.NotFoundException;
import entity.Doctor;

public class DoctorDAOImpl implements IDoctorDAO {
    
    @Override
    public void add(Doctor medico) {
        DataManager.transact(session -> {
            session.save(medico);
        });
    }
    
    @Override
    public Optional<Doctor> findById(int id, boolean searchDisabled) {
        final Optional<Doctor> cfMedico = new Optional<>(null);
        DataManager.run(session -> {
            String hql = "FROM Doctor WHERE id = :id" + (searchDisabled ? "" : " AND active");
            Query query = session.createQuery(hql);
            query.setParameter("id", id);
            cfMedico.set((Doctor) query.uniqueResult());
        });
        return cfMedico;
    }
    
    @Override
    public List<Doctor> list() {
        return list(1, 15);
    }
    
    @Override
    public List<Doctor> list(int page, int size) {
        return list(page, size, false);
    }
    

	@SuppressWarnings("unchecked")
	@Override
	public List<Doctor> list(int page, int size, boolean includeInactiveRecords) {
		 final Optional<List<Doctor>> optionalList = new Optional<>();
	        DataManager.run(session -> {
	            String hql = "FROM Doctor" + (includeInactiveRecords ? "" : " WHERE active");
	            Query query = session.createQuery(hql);
	            query.setFirstResult((page - 1) * size);
	            query.setMaxResults(size);
	            optionalList.set(query.list());
	        });
	        return optionalList.get();
	}
    
    @Override
    public void update(Doctor medico) {
        DataManager.transact(session -> {
            session.update(medico);
        });
    }
    
    @Override
    @Deprecated
    public void erase(Doctor medico) {
        DataManager.transact(session -> {
            session.delete(medico);
        });
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> listOnlyFileNumbersAndNames() {
		final Optional<List<Object[]>> optionalList = new Optional<>();
        DataManager.run(session -> {
            String hql = "SELECT m.file, m.name, m.surname FROM Doctor m ORDER BY m.file ASC";
            Query query = session.createQuery(hql);
            optionalList.set(query.list());
        });
        return optionalList.get();
	}

	@Override
	public Doctor findDoctorWithHighestFileNumber() {
		List<Doctor> list = this.listOrderByFileDescending(1, 1);
		return list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> listOnlyFileNumbers(){
		final Optional<List<Integer>> optionalMedicos = new Optional<>();
        DataManager.run(session -> {
            String hql = "SELECT m.file FROM Doctor m";
            Query query = session.createQuery(hql);
            optionalMedicos.set(query.list());
        });
        return optionalMedicos.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Doctor> listOrderByFileDescending(int page, int size) {
		final Optional<List<Doctor>> optionalMedicos = new Optional<>();
		DataManager.run(session -> {
			String hql = "SELECT m FROM Doctor m ORDER BY m.file DESC";
			Query query = session.createQuery(hql);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
			optionalMedicos.set(query.list());
		});
		return optionalMedicos.get();
	}

	@Override
	public List<Doctor> listOrderByFileDescending() {
		return listOrderByFileDescending(1, 10);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getAppointmentsByDoctorAndDate(int legajo, LocalDate fecha) {
        final Optional<List<Object[]>> optional = new Optional<>();
        DataManager.run(session -> {
            String hql = "SELECT m.file, t.fecha, t.estado " +
                         "FROM Turno t INNER JOIN t.medico m " +
                         "WHERE m.file = :legajo AND t.fecha = :fecha";
            Query query = session.createQuery(hql);
            query.setParameter("legajo", legajo);
            query.setParameter("fecha", java.sql.Date.valueOf(fecha));
            optional.set(query.list());
        });
        return optional.get();
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getAppointmentsByDoctorAndDateRange(int legajo, LocalDate fechaInicio, LocalDate fechaFin) {
	    final Optional<List<Object[]>> cfList = new Optional<>();
	    DataManager.run(session -> {
	        String hql = "SELECT m.file, t.fecha, t.estado " +
	                     "FROM Turno t INNER JOIN t.medico m " +
	                     "WHERE m.file = :legajo AND t.fecha BETWEEN :fechaInicio AND :fechaFin";
	        Query query = session.createQuery(hql);
	        query.setParameter("legajo", legajo);
	        query.setParameter("fechaInicio", java.sql.Date.valueOf(fechaInicio));
	        query.setParameter("fechaFin", java.sql.Date.valueOf(fechaFin));
	        cfList.set(query.list());
	    });
	    return cfList.get();
	}

	@Override
	public Optional<Doctor> findByFile(int file) {
		final Optional<Doctor> optional = new Optional<>();
		DataManager.run(session -> {
			String hql = "SELECT m FROM Doctor m WHERE m.file = :legajo";
			Query query = session.createQuery(hql);
			query.setParameter("legajo", file);
			optional.set((Doctor) query.uniqueResult()); 
		});
		return optional;
	}

	@Override
	public Optional<Doctor> findById(int id) {
		return findById(id, false);
	}
	
	private void updateStatus(int id, boolean newStatus) throws NotFoundException {
		Optional<Doctor> search = findById(id, newStatus);
    	if(search.isEmpty()) throw new NotFoundException();
        DataManager.transact(session -> {
        	Doctor original = search.get();
        	original.setActive(newStatus);
            session.update(original);
        });
	}

	@Override
	public void disable(int id) throws NotFoundException {
		updateStatus(id, false);
	}

	@Override
	public void enable(int id) throws NotFoundException {
		updateStatus(id, true);
	}
}