package core.storage;

import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import core.Environment;

public class DatabaseManager {
    // Hmm, must do some bonecp working
	
	public SQLQuery executeQuery(String Query) throws Exception
	{
		Session Se = Environment.sessionFactory.openSession();
		SQLQuery Q = Se.createSQLQuery(Query);
		return Q;        
	}
}
