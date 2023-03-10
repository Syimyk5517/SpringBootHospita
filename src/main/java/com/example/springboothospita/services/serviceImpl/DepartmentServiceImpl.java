package com.example.springboothospita.services.serviceImpl;

import com.example.springboothospita.models.Appointment;
import com.example.springboothospita.models.Department;
import com.example.springboothospita.models.Doctor;
import com.example.springboothospita.models.Hospital;
import com.example.springboothospita.repository.AppointmentRepo;
import com.example.springboothospita.repository.DepartmentRepo;
import com.example.springboothospita.repository.DoctorRepo;
import com.example.springboothospita.repository.HospitalRepo;
import com.example.springboothospita.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepo departmentRepo;
    private final HospitalRepo hospitalRepo;
    private final DoctorRepo doctorRepo;
    private final AppointmentRepo appointmentRepo ;

    @Override
    public List<Department> getAll(Long id) {
        return departmentRepo.getAllByHospitalId(id);
    }

    @Override
    @Transactional
    public void save(Long id, Department department)  {
        Hospital hospital = hospitalRepo.findById(id).orElseThrow();
//      for (Department dep : departmentRepo.getAll(id)) {
//           if (dep.getName().equalsIgnoreCase(department.getName())) {
//              throw new BadRequestExseption("");
//           } else {
                hospital.addDepartment(department);
                department.setHospital(hospital);
                departmentRepo.save(department);
//          }
//      }
    }


    @Override
    public Department finById(Long id) {
       return departmentRepo.findById(id).orElseThrow();
    }

    @Override
    public void deleteById(Long id) {
      Department department = departmentRepo.findById(id).orElseThrow();
      Hospital hospital = department.getHospital();
      List<Appointment> appointments = appointmentRepo.findAllByHospitalId(hospital.getId());
      List<Appointment> appointmentList = new ArrayList<>();
      for (Appointment appointment : appointments){
          if (appointment.getDepartment().getId().equals(id)){
              appointmentList.add(appointment);
          }
      }
      appointmentList.forEach(appointment -> appointment.getDoctor().setAppointments(null));
      appointmentList.forEach(appointment -> appointment.getPatient().setAppointments(null));
      hospital.getAppointments().removeAll(appointmentList);
        for (int i = 0; i < appointmentList.size(); i++) {
            appointmentRepo.deleteById(appointmentList.get(i).getId());
        }
        departmentRepo.deleteById(id);
    }

    @Override
    public void update(Long departmentId, Department department) {
        Department oldDepartment = finById(departmentId);
        oldDepartment.setName(department.getName());
          departmentRepo.save(oldDepartment);
    }

    @Override
    public List<Department> getAllDepartmentByDoctorId(Long id) {
        return departmentRepo.getAllDepartmentDoctorById(id);
    }

}
