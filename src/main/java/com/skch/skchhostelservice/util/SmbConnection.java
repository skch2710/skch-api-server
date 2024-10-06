package com.skch.skchhostelservice.util;
import java.util.Properties;

import jcifs.CIFSContext;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;

public class SmbConnection {

    public static void main(String[] args) {
        try {
            // Define SMB credentials
            String username = "sathish_ch";
            String password = "Smb@1234";
            String domain = ""; // If there's no domain, leave it empty
            String smbUrl = "smb://192.168.0.106//SharedFolder/";//192.168.0.106
            

            // Create configuration using properties
            Properties prop = new Properties();
            prop.setProperty("jcifs.smb.client.enableSMB2", "true");
            CIFSContext baseContext = new BaseContext(new PropertyConfiguration(prop));

            // Use NtlmPasswordAuthenticator to set credentials
            NtlmPasswordAuthenticator authenticator = new NtlmPasswordAuthenticator(domain, username, password);
            CIFSContext authContext = baseContext.withCredentials(authenticator);

            // Connect to the SMB shared folder
            SmbFile smbFolder = new SmbFile(smbUrl, authContext);

            if (smbFolder.exists()) {
                System.out.println("Connected to SMB share!");
                SmbFile[] listFiles = smbFolder.listFiles();
                for (SmbFile file : listFiles) {
                	System.out.println("File Name : "+file.getName());
				}
            } else {
                System.out.println("SMB share not found.");
            }
            
            smbFolder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
