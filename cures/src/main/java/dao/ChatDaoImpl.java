package dao;

import java.io.IOException;
import java.lang.reflect.Type;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import util.HibernateUtil;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Chat;

import model.Registration;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;
import util.Constant;
import util.Encryption;
import util.HibernateUtil;

public class ChatDaoImpl {

	public static MemcachedClient mcc = null;
	// static Session session = HibernateUtil.buildSessionFactory();
	@Transactional
	public static Integer Chat_Store(Integer chat_id, HashMap<String, Object> chatMap) {
//		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

		Session session = HibernateUtil.buildSessionFactory();

		session.beginTransaction();
		System.out.println(chat_id);
		System.out.println(session.isOpen());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		chatMap.put("time",timestamp);
		
		
		int ret = 0;
		String insertStr = "INSERT into dp_chat_history " + "(chat_id,";

		String insertStr_values = "(" + chat_id + ",";
		try {
			// Iterating HashMap through for loop
			for (Map.Entry<String, Object> chatDetail : chatMap.entrySet()) {

				insertStr += chatDetail.getKey() + ", ";

				if (chatDetail.getValue() instanceof Integer) {
					insertStr_values += (Integer) (chatDetail.getValue()) + ", ";
				} else if (chatDetail.getValue() instanceof Timestamp) {
					insertStr_values += "'" + (Timestamp) (chatDetail.getValue()) + "' , ";
				}else {
					insertStr_values += "'" + (String) (chatDetail.getValue()) + "' , ";
				}
				
			}

			insertStr = insertStr.substring(0, insertStr.lastIndexOf(","));
			insertStr_values = insertStr_values.substring(0, insertStr_values.lastIndexOf(","));
			String completInsertStr = insertStr + ")" + " values " + insertStr_values + " );";
			System.out.println(completInsertStr);

			Query query = session.createNativeQuery(completInsertStr);

			// needs other condition too but unable to find correct column
			ret = query.executeUpdate();
			session.getTransaction().commit();
/*
			 // Store the data in Memcache along with the timestamp
	        if(mcc == null) {
	            initializeCacheClient();
	        }
	        
	 //       mcc.delete("Chat_id" + "_" + chat_id);
	 //      // Retrieve the existing chat data from memcached
	        String cacheString = (String) mcc.get("Chat_id_" + chat_id);
	        System.out.println("Cache String: " + cacheString); // 
	        HashMap<String, Object> chatData = null;
	        if (cacheString != null) {
	            chatData = new Gson().fromJson(cacheString, new TypeToken<HashMap<String, Object>>() {}.getType());
	        } else {
	            chatData = new HashMap<String, Object>();
	        }
	        
	        
	        
	        // Get the existing message list or create a new one if it doesn't exist
	     // Get the existing message list or create a new one if it doesn't exist
	        Object messagesObj = chatData.get("messages");
	        ArrayList<HashMap<String, Object>> messages = new ArrayList<>();
	        if (messagesObj != null) {
	            // Check if the type is ArrayList or HashMap
	            if (messagesObj instanceof ArrayList) {
	                // If it is an ArrayList, cast it and use it as is
	                messages = (ArrayList<HashMap<String, Object>>) messagesObj;
	            } else if (messagesObj instanceof HashMap) {
	                // If it is a HashMap, create an ArrayList and add the existing message
	                HashMap<String, Object> messageMap = (HashMap<String, Object>) messagesObj;
	                messages.add(messageMap);
	            }
	        }

	        // Append the new message to the existing messages
	        messages.add(chatMap);
	        chatData.put("messages", messages);

	        // Add the updated chat data to memcached
	        Gson gson = new GsonBuilder().create();
	        String jsonData = gson.toJson(chatData);
	        mcc.set("Chat_id_" + chat_id, 3600, jsonData);
		System.out.println("Added to memcached");
	//		session.close();

		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
//		session.getTransaction().commit(); session.close();
		}
		
	*/	
		return ret;
	}

	public static Integer ChatStore() {
		Session session = HibernateUtil.buildSessionFactory();
		// System.out.println(session.isOpen());

		Chat chat = new Chat();
		Date d1 = new Date();

		chat.setDate(d1);

		Transaction tx = session.beginTransaction();
		session.save(chat);
//		tx.commit();

		// session.close();

		System.out.println(chat.getChat_id());
		return chat.getChat_id();
	}

	public static Integer DoctorLeads(Integer doc_id) {
		Session session = HibernateUtil.buildSessionFactory();

		session.beginTransaction();

		int ret = 0;
		String insertStr = "INSERT into dp_doctor_leads " + "(doc_id)" + "values(" + doc_id + ");";
		System.out.println(insertStr);
		
		Query query = session.createNativeQuery(insertStr);

		// needs other condition too but unable to find correct column
		try {
		ret = query.executeUpdate();
		}catch (Exception e) {
			    session.getTransaction().rollback();
			    }
			
		session.getTransaction().commit();

//		session.close();
		return ret;
	}
	
	public static MemcachedClient initializeCacheClient() {
		try {
			Constant.log("Trying Connection to Memcache server", 0);
			mcc = new MemcachedClient(
					new ConnectionFactoryBuilder().setDaemon(true).setFailureMode(FailureMode.Retry).build(),
					AddrUtil.getAddresses(Constant.ADDRESS));
			Constant.log("Connection to Memcache server Sucessful", 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Constant.log("Connection to Memcache server UN-Sucessful", 3);
		}
		return mcc;
	}

	public static List findChatInCache(int chat_id) {
		String cacheString = null;

		// This is the ADDRESS OF MEMCACHE
		// TODO: Move to a Config Entry in Web.xml
		if (mcc == null) {
			initializeCacheClient();
		}
		Constant.log("Getting chat_id from MemCache", 0);
		
		 cacheString = (String) mcc.get("Chat_id_" + chat_id);
	        System.out.println("Cache String: " + cacheString); // 
	        HashMap<String, Object> chatData = null;
	        if (cacheString != null) {
	            chatData = new Gson().fromJson(cacheString, new TypeToken<HashMap<String, Object>>() {}.getType());
	            Constant.log("Found In MemCache:" + cacheString, 0);
	        } else {
	            chatData = new HashMap<String, Object>();
	        }
	        
	     // Extract messages from chat data and render them
		     List<String> allMessages = new ArrayList<String>();
		        if (chatData != null && chatData.containsKey("messages")) {
		            List<Map<String, Object>> messages1 = (List<Map<String, Object>>) chatData.get("messages");
		            for (Map<String, Object> message : messages1) {
		                String fromId = message.get("from_id").toString();
		                String toId = message.get("to_id").toString();
		                String time = message.get("time").toString();
		                String enmsg = message.get("message").toString();
		                
		                final String secretKey = Constant.SECRETE;
		    			Encryption encrypt = new Encryption();
		    		
		    			String msg = encrypt.decrypt(enmsg, secretKey);
		                
		                String renderedMsg ="Time " +  "[" + time + "] " + "From: " + fromId + " To: " +  toId + " Message:" +  msg;
		                allMessages.add(renderedMsg);
		            }
		        }
		        System.out.println(allMessages);
	   
	        
	        
		return allMessages;
	}
	
	public static String Chat_ID_Search(Integer chat_id)
	{
	
		Constant.log("Got Req for Chat_ID: "+chat_id, 1);
		List<String> allMessages = findChatInCache(chat_id);	
		
		
		String jsondata = null;
		if(allMessages.size() == 0 ){
			//Chat Not Found in MemCache
			Constant.log("Got Null From MemCache on the Chat:"+chat_id, 1);
		List chat=Chat_Search(chat_id);
		
		Gson gson = new GsonBuilder().serializeNulls().create();	
		jsondata = gson.toJson(chat);
	//	mcc.add("Chat_id"+"_"+chat_id,360000 ,jsondata).getStatus();
			
		}
		
		else
		{
			Constant.log("Found Chat in Memcache and serving from there", 1);
			jsondata = new Gson().toJson(allMessages);
			
			
		}
		
		return jsondata ;
	}
	
	
	
	public static List Chat_Search(Integer chat_id) {
		Session session = HibernateUtil.buildSessionFactory();

		Query query = session.createNativeQuery(
				"SELECT chat.chat_id,chat.to_id as to_id,reg.first_name,reg.last_name,reg.registration_type,  (SELECT reg.first_name FROM registration as reg where reg.registration_id=to_id) as user_first,(SELECT reg.last_name FROM registration as reg where reg.registration_id=to_id) as user_last, chat.message,chat.time,chat.from_id\r\n"
				+ "FROM registration as reg\r\n"
				+ "LEFT JOIN dp_chat_history as chat ON chat.from_id = reg.registration_id where chat.chat_id= \r\n"
				+ "" + chat_id + " ;");
		
		List<Object[]> results = (List<Object[]>) query.getResultList();
		System.out.println("Getting results:" );
		List hmFinal = new ArrayList();
		for (Object[] objects : results) {
			HashMap hm = new HashMap();
			
			Integer Chat_id = (Integer) objects[0];
			Integer To_id=(Integer) objects[1];

			String from_first = (String) objects[2];
			String from_last = (String) objects[3];
			Integer from_reg_type=(Integer) objects[4];
			
			String to_first = (String) objects[5];
			String to_last = (String) objects[6];
			
			String demsg = (String) objects[7];
			
			final String secretKey = Constant.SECRETE;
			Encryption encrypt = new Encryption();
		
			String message = encrypt.decrypt(demsg, secretKey);
			Timestamp time=(Timestamp) objects[8];
			Integer From_id=(Integer) objects[9];
			
			hm.put("Chat_id", Chat_id);
			hm.put("From", from_first + " " + from_last);
			hm.put("To", to_first + " " + to_last);
			hm.put("Message", message);
			hm.put("Time", time);
			hm.put("From_id", From_id);
			hm.put("From_reg_type", from_reg_type);
			hm.put("To_id", To_id);
			
			hmFinal.add(hm);
			
		}

		return hmFinal;

	}
	public static List Chat_History(Integer doc_id, String date) {
		Session session = HibernateUtil.buildSessionFactory();
	
		Query query = session.createNativeQuery(
				"Select chat_id,from_id,to_id,message  from dp_chat_history where (from_id=" + doc_id + " or to_id=" + doc_id + ") and Date(time)='" + date + "' ;");
		
		
		List<Object[]> results = (List<Object[]>) query.getResultList();
		
		List hmFinal = new ArrayList();
		for (Object[] objects : results) {
			
			HashMap hm = new HashMap();

			Integer Chat_id = (Integer) objects[0];

			Integer from = (Integer) objects[1];
			Integer to = (Integer) objects[2];
			String message=(String) objects[3];
		//	Date date1=(Date)objects[4];
			hm.put("Chat_id", Chat_id);
			hm.put("From", from);
			hm.put("To", to);
			hm.put("Message", message);
	//		hm.put("Date", date1);
			
			hmFinal.add(hm);

		}
		System.out.println(hmFinal);
		return hmFinal;

	}
	
	public static Integer Chat_Archive() {
		Session session = HibernateUtil.buildSessionFactory();

		session.beginTransaction();

		int ret = 0;
		String Str = "INSERT into dp_chat_archive select * from allcures1.dp_chat_history;";
		System.out.println(Str);
		
		Query query = session.createNativeQuery(Str);

		// needs other condition too but unable to find correct column
		ret = query.executeUpdate();
//		session.getTransaction().commit();

//		session.close();
		return 1;
	}
	
	public static Integer save_doc_path(Integer chat_id, String path) {
		Session session = HibernateUtil.buildSessionFactory();

		session.beginTransaction();

		int ret = 0;
	
		System.out.println("Path is"+ path);
		 
        path.toString();
		
		Query query = session.createNativeQuery(
				"Update dp_chat_archive set document_path=\"  '" + path + "'  \"where chat_id=" + chat_id + " ;");

		// needs other condition too but unable to find correct column
		ret = query.executeUpdate();
//		session.getTransaction().commit();

//		session.close();
		
		return 1;
	}
	
	public static List ChatStored(Integer from_id, Integer to_id) {
		Session session = HibernateUtil.buildSessionFactory();
//		session.beginTransaction();
		List hmFinal = new ArrayList();

		// System.out.println(session.isOpen());
		int res = 0;
		Query query = session.createNativeQuery(
				"Select chat_id  from dp_chat where (from_id=" + from_id + " and to_id=" + to_id + ");");
		
		try {
			res = (int) query.getSingleResult();
			
			System.out.println(res);
			
		} catch (NoResultException e) {
		e.printStackTrace();
		}
		if (res == 0) {
			
			HashMap hm = new HashMap();
			hm.put("Chat_id", null);
			
			hmFinal.add(hm);
			
		}

		else {
			
			hmFinal = Chat_Search(res);
		}

//		session.getTransaction().commit();

//		session.close();

		return hmFinal;
	}
	
	public static Integer ChatStart(Integer from_id, Integer to_id) {
		Session session = HibernateUtil.buildSessionFactory();
		session.beginTransaction();
		
			System.out.println("NEW CHAT");
			Date d1 = new Date();
			int ret=0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String Date1 = dateFormat.format(d1);
			Query query2 = session.createNativeQuery("insert into dp_chat(from_id,to_id,date) values("
					 + from_id + "," + to_id + ", '" + Date1 + "' );");
			try {
				ret = query2.executeUpdate();
				System.out.println(ret);
				session.getTransaction().commit();
			}
			catch (Exception e) {
			    session.getTransaction().rollback();
			    }
	//		session.getTransaction().commit();

	//		session.close();
			return ret;
}
	
	public static List Chat_List(Integer user_id) {
		
		Session session = HibernateUtil.buildSessionFactory();
		
		Query query = session.createNativeQuery(
				"SELECT 	reg.first_name,reg.last_name, reg.registration_type , reg.rowno, reg.registration_id as user,\r\n"
				+ "	 MAX(h.time) AS last_time, \r\n"
				+ "       h.message AS last_message\r\n"
				+ "       \r\n"
				+ "FROM dp_chat_history AS h\r\n"
				+ "INNER JOIN (\r\n"
				+ "    SELECT chat_id,to_id as user, MAX(time) AS last_time\r\n"
				+ "    FROM dp_chat_history\r\n"
				+ "    WHERE (from_id = " + user_id + " OR to_id = " + user_id + ")\r\n"
				+ "    GROUP BY chat_id\r\n"
				+ ") AS m\r\n"
				+ "\r\n"
				+ "INNER JOIN (\r\n"
				+ " Select r.first_name,r.last_name, r.registration_type ,r.registration_id, r.rowno FROM registration as r\r\n"
				+ "   \r\n"
				+ ") AS reg\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "ON h.chat_id = m.chat_id AND h.time = m.last_time\r\n"
				+ "WHERE (h.from_id = " + user_id + " OR h.to_id = " + user_id + ") AND reg.registration_id=m.user\r\n"
				+ "GROUP BY h.chat_id\r\n"
				+ "ORDER BY last_time DESC;\r\n"
				+ "");
		
		List<Object[]> results = (List<Object[]>) query.getResultList();
		System.out.println(results.size());
		List hmFinal = new ArrayList();
		for (Object[] objects : results) {
			LinkedHashMap<String, Object> hm = new LinkedHashMap<>();   
			// add linkedhashmap to preserve the order
			String first_name = (String) objects[0];
			
			String last_name = (String) objects[1];
			
	
			Integer row_no = (Integer) objects[3];
			
			Integer user=(Integer) objects[4];
					
			Timestamp time=(Timestamp) objects[5];
			String message=(String) objects[6];
			
			hm.put("First_name", first_name);
			hm.put("Last_name",last_name);
			hm.put("User",user);
			hm.put("Rowno", row_no);
			hm.put("Message", message);
			hm.put("Time", time);
			
			hmFinal.add(hm);
			
		}
		
		return hmFinal;

	}

	
}
