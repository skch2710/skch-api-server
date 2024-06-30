package com.skch.skchhostelservice.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skch.skchhostelservice.dto.HostellerDTO;
import com.skch.skchhostelservice.util.Utility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HostelBatch {

	private final JdbcTemplate jdbcTemplate;
	private final int batchSize = 10;

	public HostelBatch(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private String insertQuery = "INSERT INTO hostel.hostellers(full_name, email_id, phone_number, fee, "
			+ "joining_date, address, proof, reason, vacated_date, active, "
			+ "created_by_id, created_date, modified_by_id, modified_date)\r\n"
			+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	@Transactional
    public void saveInBatch(ArrayList<HostellerDTO> list) {
        try {
            jdbcTemplate.batchUpdate(this.insertQuery, list, this.batchSize,
                    new ParameterizedPreparedStatementSetter<HostellerDTO>() {
                        @Override
                        public void setValues(PreparedStatement ps, HostellerDTO model) throws SQLException {
							ps.setObject(1, model.getFullName(), Types.VARCHAR);
							ps.setObject(2, model.getEmailId(), Types.VARCHAR);
							ps.setObject(3, model.getPhoneNumber(), Types.VARCHAR);
							ps.setObject(4, Utility.toBigDecimal(model.getFee()), Types.NUMERIC);
//							ps.setObject(5, DateUtility.stringToDateTimes(model.getJoiningDate(),"dd-MM-yyyy"), Types.TIMESTAMP);
							ps.setObject(5, null, Types.NULL);
							ps.setObject(6, model.getAddress(), Types.VARCHAR);
							ps.setObject(7, model.getProof(), Types.VARCHAR);
							ps.setObject(8, model.getReason(), Types.VARCHAR);
							
//							if(model.getVacatedDate() != null && !model.getVacatedDate().isBlank()) {
//								ps.setObject(9, DateUtility.stringToDateTimes(model.getVacatedDate(),"dd-MM-yyyy"), Types.TIMESTAMP);
//							}else {
								ps.setObject(9, null, Types.NULL);
//							}
							ps.setObject(10, model.getActive().equalsIgnoreCase("Yes"), Types.BOOLEAN);
							ps.setObject(11, model.getCreatedById(), Types.BIGINT);
							ps.setObject(12, model.getCreatedDate(), Types.TIMESTAMP);
							ps.setObject(13, model.getModifiedById(), Types.BIGINT);
							ps.setObject(14, model.getModifiedDate(), Types.TIMESTAMP);
						}
					});
            log.info("Batch insert completed for batch size: " + list.size());
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

}
