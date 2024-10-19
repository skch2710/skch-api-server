package com.skch.skchhostelservice.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmbService {
	
	private static CIFSContext authContext;

    private static String domain = "192.168.0.106";
    private static String username = "sathish_ch";
    private static String password = "Smb@1234";

//    @PostConstruct
    public static void initialize(String domain,String username,String password) {
        // Initialize the CIFS context and authenticator
        CIFSContext baseContext = SingletonContext.getInstance();
        NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(domain, username, password);
        authContext = baseContext.withCredentials(auth);
        System.out.println("SMB Configuration initialized.");
    }

    public static CIFSContext getAuthContext() {
        return authContext;
    }
    
    public static void writeFileToSmbFolder(String folder, String filename, byte[] fileContent) {
        SmbFile smbFile = null;
        OutputStream os = null;
        try {
			String smbUrl = "smb://" + domain +"//"+ folder + filename;
            // Create a SmbFile object for the file you want to write to
            smbFile = new SmbFile(smbUrl, SmbService.getAuthContext());

            // Open an output stream to the SMB file
            os = smbFile.getOutputStream();

            // Write the byte[] content to the SMB file
            os.write(fileContent);
            os.flush();
            System.out.println("File written successfully to " + smbFile.getPath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the output stream and the SMB file
            try {
                if (os != null) {
                    os.close();
                }
                if (smbFile != null) {
                    smbFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void readFiles(String folder) {
        SmbFile smbFolder = null;
        try {
        	String smbUrl = "smb://" + domain +"//"+ folder;
            // Create a SmbFile object for the file you want to write to
        	smbFolder = new SmbFile(smbUrl, SmbService.getAuthContext());
        	
        	if(smbFolder.exists()) {
        		
        		System.out.println("Connected to SMB share!");
                SmbFile[] listFiles = smbFolder.listFiles();
                for (SmbFile file : listFiles) {
                	System.out.println("File Name : "+file.getName());
				}
        		
        	}else {
        		System.out.println("Folder Not Found...");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (smbFolder != null) {
                	smbFolder.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void deleteFiles(String folder) {
        SmbFile smbFolder = null;
        try {
        	String smbUrl = "smb://" + domain +"//"+ folder;
            // Create a SmbFile object for the file you want to write to
        	smbFolder = new SmbFile(smbUrl, SmbService.getAuthContext());
        	
        	if(smbFolder.exists()) {
        		System.out.println("Connected to SMB share!");
                SmbFile[] listFiles = smbFolder.listFiles();
                for (SmbFile file : listFiles) {
                	System.out.println("File Name : "+file.getName());
                	
                	LocalDateTime dateTime = DateUtility.getLongMilli(file.lastModified());
                	System.out.println("Last Modified Date: " + dateTime);
                	
                	if(dateTime.isBefore(LocalDateTime.now().minusDays(10))) {
                		System.out.println("File is 10 Days Old " + dateTime);
                		try {
							file.delete();
							log.info("File Deleted....");
						} catch (Exception e) {
							log.error("Error in file Delete...");
						}
                	}
				}
        	}else {
        		System.out.println("Folder Not Found...");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (smbFolder != null) {
                	smbFolder.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void moveFiles(String filePath) {
        SmbFile sourceFile = null;
        SmbFile targetFile = null;
        try {
        	String smbUrl = "smb://" + domain +"//"+ filePath;
        	sourceFile = new SmbFile(smbUrl, SmbService.getAuthContext());
        	targetFile = new SmbFile(smbUrl.replaceAll("Queue", "Success"), SmbService.getAuthContext());
        	
        	if(sourceFile.exists()) {
        		System.out.println("Connected to SMB share!");
        		sourceFile.renameTo(targetFile);
        	}else {
        		System.out.println("Folder Not Found...");
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (sourceFile != null) {
                	sourceFile.close();
                }
                if(targetFile != null) {
                	targetFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
        	
        	SmbService.initialize(domain, username, password);
        	
        	String folder = "SharedFolder/";
        	String fileName = "example_excel_3.xlsx";
        	ByteArrayOutputStream bao = Utility.createExcel();
        	
//        	writeFileToSmbFolder(folder,fileName,bao.toByteArray());
//        	readFiles(folder);
//        	deleteFiles(folder);
        	
        	String queuefile = "SharedFolder/Queue/example_excel_1.xlsx";
        	moveFiles(queuefile);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
