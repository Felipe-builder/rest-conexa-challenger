package com.felipe.unittests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.felipe.exceptions.BadRequestException;
import com.felipe.mapper.PatientMapper;
import com.felipe.model.Patient;
import com.felipe.model.dto.v1.PatientDTO;
import com.felipe.repositories.PatientRepository;
import com.felipe.service.PatientService;
import com.felipe.unittests.mapper.mocks.MockPatient;
import com.felipe.util.MessageUtils;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

	private Logger logger = Logger.getLogger(PatientService.class.getName());

	@Mock
	private PatientRepository repository;

//	@Autowired
	private PatientMapper mapper = new PatientMapper();

	@Autowired
	@InjectMocks
	private PatientService service;

	private PatientDTO patientDto;

	private Patient patient;

	MockPatient inputObject = new MockPatient();;

	@BeforeEach
	public void setUp() {
		patient = inputObject.mockRandomEntity(true);
		patientDto = mapper.toDto(patient);
	}

	@DisplayName("JUnit test for Given Patients Object when Save Patient then Return Patient Object")
	@Test
	void testGivenPatientObject_whenSavePatient_thenReturnPatientObject() throws Exception {
		logger.info("JUnit test for Given Patients Object when Save Patient then Return Patient Object");
		
		given(repository.findByEmail(anyString())).willReturn(Optional.empty());
		given(repository.save(patient)).willReturn(patient);

		logger.info(patient.toString());
		logger.info("DTO => " + patientDto.toString());

		PatientDTO patientDtoCreated = service.create(patientDto);
		logger.info("CREATED => " + patientDtoCreated.toString());

		assertNotNull(patientDtoCreated);
		assertTrue(!patientDtoCreated.getKey().toString().isEmpty());
		assertEquals(patient.getFullName(), patientDtoCreated.getFullName());

	}

	@DisplayName("JUnit test for Given Existing Email when Save Patient then Throws Exception")
	@Test
	void testGivenExistingEmail_whenSavePatient_thenThrowsException() throws Exception {
		logger.info("JUnit test for Given Existing Emai when Save Patient then Throws Exception");

		// Set the email of the PatientDTO to "songpagaciv.2961@example.com"
		patientDto.setEmail("songpagaciv.2961@example.com");

		// Mock the behavior of the repository
		given(repository.findByEmail("songpagaciv.2961@example.com")).willReturn(Optional.of(patient));

		BadRequestException exception = assertThrows(BadRequestException.class, () -> {
			logger.info("Before service.create");
			service.create(patientDto);
			logger.info("After service.create");
		});

		assertEquals("Email " + MessageUtils.RECORDS_ALREADY_EXIST + ": " + patientDto.getEmail(), exception.getMessage());
		verify(repository, never()).save(any(Patient.class));
	}
	
	@DisplayName("JUnit test for Given Patients List when Find All Patients Then Return Patients List")
	@Test
	void testGivenPatientsList_whenFindAllPatients_thenReturnPatientsList()  {
		logger.info("JUnit test for Given Patients List when Find All Patients Then Return Patients List");

		// Mock the behavior of the repository
		given(repository.findAll()).willReturn(inputObject.mockRandomEntityList(3, true));

		List<PatientDTO> patientsDtoList = service.findAll();
		logger.info("SIZE => " + patientsDtoList.size());

		assertNotNull(patientsDtoList);
		assertEquals(3, patientsDtoList.size());
	}
	
	@DisplayName("JUnit test for Given Empty Patients List when Find All Patients Then Return Empty Patients List")
	@Test
	void testGivenEmptyPatientsList_whenFindAllPatients_thenReturnEmptyPatientsList() throws Exception {
		logger.info("JUnit test for Given Empty Patients List when Find All Patients Then Return Empty Patients List");

		// Mock the behavior of the repository
		given(repository.findAll()).willReturn(Collections.emptyList());

		List<PatientDTO> patientsDtoList = service.findAll();
		logger.info("SIZE => " + patientsDtoList.size());

		assertTrue(patientsDtoList.isEmpty());
		assertEquals(0, patientsDtoList.size());
	}

	@DisplayName("JUnit test for Given PatientId when Find By Id Then Return Patient Object")
	@Test
	void testGivenPatientsId_whenFindById_thenReturnPatientObject() throws Exception {
		logger.info("JUnit test for Given PatientId when Find By Id Then Return Patient Object");

	    //UUID valid for mocks
	    String patientIdString = UUID.randomUUID().toString();
		
		given(repository.findById(any(UUID.class))).willReturn(Optional.of(patient));


		PatientDTO patientFound = service.findById(patientIdString);
		logger.info("FoundByID => " + patientDto.toString());
		logger.info("USER => " + patient.toString());

		assertNotNull(patientFound);
		assertTrue(!patientFound.getKey().toString().isEmpty());
		assertEquals(patient.getFullName(), patientFound.getFullName());
	}
	
	@DisplayName("JUnit test for Given Patient Object when Update Patient Then Return Update Patient Object")
	@Test
	void testGivenPatientObject_whenUpdatePatient_thenReturnUpdatePatientObject() throws Exception {
		logger.info("JUnit test for Given Object when Update Patient Then Return Update Patient Object");

		given(repository.findById(any(UUID.class))).willReturn(Optional.of(patient));
		given(repository.save(any(Patient.class))).willReturn(patient);

		String newName = "Mauricio Di Paula";
		String newEmail = "mauricio_dipaula@email.com";
		logger.info("Before Update => " + patientDto.toString());

		patient.setFullName(newName);
		patient.setEmail(newEmail);
		patientDto = mapper.toDto(patient);

		PatientDTO patientUpdated = service.update(patientDto);
		logger.info("After Update => " + patientDto.toString());

		assertNotNull(patientUpdated);
		assertEquals(newName, patientUpdated.getFullName());
		assertEquals(newEmail, patientUpdated.getEmail());
	}
	
	@DisplayName("JUnit test for Given PatientID when Delete Patient then do Nothing")
	@Test
	void testGivenPatientID_whenDeletePatient_thenDoNothing() throws Exception {
		logger.info("JUnit test for Given PatientID when Delete Patient then do Nothing");

		given(repository.findById(any(UUID.class))).willReturn(Optional.of(patient));
		willDoNothing().given(repository).delete(patient);
		logger.info("After Delete => " + patient.toString());

		service.delete(patient.getId().toString());
		
		verify(repository, times(1)).delete(patient);
	}

}
