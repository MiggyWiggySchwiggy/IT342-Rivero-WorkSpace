package edu.cit.rivero.workspace;
import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.features.space.*;
import edu.cit.rivero.workspace.features.reservation.*;
import edu.cit.rivero.workspace.features.reservation.strategy.*;
import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.security.*;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkspaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkspaceApplication.class, args);
	}

}
