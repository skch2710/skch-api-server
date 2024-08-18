package com.skch.skchhostelservice.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.dto.UsersFileDTO;
import com.skch.skchhostelservice.util.DateUtility;
import com.skch.skchhostelservice.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BatchInsert {

	private final JdbcTemplate jdbcTemplate;
	
	private final int batchSize = 1000;

	public BatchInsert(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private String insertQueryHostellers = "INSERT INTO hostel.hostellers(full_name, email_id, phone_number,dob, fee, "
			+ "joining_date, address, proof, reason, vacated_date, active, "
			+ "created_by_id, created_date, modified_by_id, modified_date)\r\n"
			+ "	VALUES (?, ?, ?, ?,? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private String insertQueryUsers = "INSERT INTO hostel.users_file_data(\r\n"
			+ "	first_name, last_name, email_id, phone_number, dob, role_name, is_active, upload_file_id, status, error_message)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	
	@Transactional
    public void saveInBatchHostellers(ArrayList<HostellerDTO> list) {
        try {
            jdbcTemplate.batchUpdate(this.insertQueryHostellers, list, this.batchSize,
                    new ParameterizedPreparedStatementSetter<HostellerDTO>() {
                        @Override
                        public void setValues(PreparedStatement ps, HostellerDTO model) throws SQLException {
							ps.setObject(1, model.getFullName(), Types.VARCHAR);
							ps.setObject(2, model.getEmailId(), Types.VARCHAR);
							ps.setObject(3, model.getPhoneNumber(), Types.VARCHAR);
							ps.setObject(4, DateUtility.stringToDate(model.getDob(),"dd-MM-yyyy"), Types.DATE);
							ps.setObject(5, Utility.toBigDecimal(model.getFee()), Types.NUMERIC);
							ps.setObject(6, DateUtility.stringToDateTimes(model.getJoiningDate(),"dd-MM-yyyy"), Types.TIMESTAMP);
							ps.setObject(7, model.getAddress(), Types.VARCHAR);
							ps.setObject(8, model.getProof(), Types.VARCHAR);
							ps.setObject(9, model.getReason(), Types.VARCHAR);
							if(model.getVacatedDate() != null && !model.getVacatedDate().isBlank()) {
								ps.setObject(10, DateUtility.stringToDateTimes(model.getVacatedDate(),"dd-MM-yyyy"), Types.TIMESTAMP);
							}else {
								ps.setObject(10, null, Types.NULL);
							}
							ps.setObject(11, model.getActive().equalsIgnoreCase("Yes"), Types.BOOLEAN);
							ps.setObject(12, model.getCreatedById(), Types.BIGINT);
							ps.setObject(13, LocalDateTime.now() , Types.TIMESTAMP);
							ps.setObject(14, model.getModifiedById(), Types.BIGINT);
							ps.setObject(15, LocalDateTime.now(), Types.TIMESTAMP);
						}
					});
//            log.info("Batch insert completed for batch size: " + list.size());
		} catch (Exception e) {
			log.error("Error in Batch Insert :: " + e);
		}
	}

//	@Transactional
//	public void saveInBatch(List<HostellerDTO> list) {
//
//		jdbcTemplate.batchUpdate(this.insertQuery, new BatchPreparedStatementSetter() {
//
//			@Override
//			public void setValues(PreparedStatement ps, int i) throws SQLException, DataAccessException {
//				HostellerDTO model = list.get(i);
//				ps.setObject(1, model.getFullName(), Types.VARCHAR);
//				ps.setObject(2, model.getEmailId(), Types.VARCHAR);
//				ps.setObject(3, model.getPhoneNumber(), Types.VARCHAR);
//				
//			}
//
//			@Override
//			public int getBatchSize() {
//				return list.size();
//			}
//		});
//	}
	
	/**
	 * Method to Insert Users in Batch Update
	 * @param list
	 */
	@Transactional
	public void saveInBatchUsers(ArrayList<UsersFileDTO> list) {
		try {
			jdbcTemplate.batchUpdate(this.insertQueryUsers, list, this.batchSize,
					new ParameterizedPreparedStatementSetter<UsersFileDTO>() {
						@Override
						public void setValues(PreparedStatement ps, UsersFileDTO model) throws SQLException {
							ps.setObject(1, model.getFirstName(), Types.VARCHAR);
							ps.setObject(2, model.getLastName(), Types.VARCHAR);
							ps.setObject(3, model.getEmailId(), Types.VARCHAR);
							ps.setObject(4, model.getPhoneNumber(), Types.VARCHAR);
							ps.setObject(5, model.getDob(), Types.VARCHAR);
							ps.setObject(6, model.getRoleName(), Types.VARCHAR);
							ps.setObject(7, model.getActive(), Types.VARCHAR);
							ps.setObject(8, model.getUploadFileId(), Types.VARCHAR);
							ps.setObject(9, model.getStatus(), Types.VARCHAR);
							ps.setObject(10, model.getErrorMessage(), Types.VARCHAR);
						}
					});
		} catch (Exception e) {
			log.error("Error in Batch saveInBatchUsers :: ", e);
		}
	}

}
