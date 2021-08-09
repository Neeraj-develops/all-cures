package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import model.Article;
import util.ArticleUtils;
import util.HibernateUtil;

@Component
public class RatingDaoImpl {

	public static List findRatingByIdandTypeandRatedByandRatedByType(int targetid, int targettypeid, int ratedById, int ratedByTypeId) {
		// creating seession factory object
		Session factory = HibernateUtil.buildSessionFactory();

		Session session = factory;

		// creating transaction object
		// Transaction trans = (Transaction) session.beginTransaction();

		Query query = session.createNativeQuery("SELECT `doctorsrating`.`rate_id`,\r\n"
				+ "    `doctorsrating`.`comments`,\r\n" + "    `doctorsrating`.`ratedBy_id`,\r\n"
				+ "    `doctorsrating`.`ratedBy_type_id`,\r\n" + "    `doctorsrating`.`target_id`,\r\n"
				+ "    `doctorsrating`.`target_type_id`,\r\n" + "    `doctorsrating`.`ratingVal`\r\n"
				+ " FROM doctorsrating where target_id=" + targetid 
				+ " and target_type_id=" + targettypeid 
				+ " and ratedBy_id ="+ratedById
				+ " and ratedBy_type_id = "+ratedByTypeId+";");
		
		List<Object[]> results = (List<Object[]>) query.getResultList();
		List hmFinal = new ArrayList();
		for (Object[] objects : results) {
			HashMap hm = new HashMap();
			Integer rate_id = (Integer) objects[0];
			String comments = (String) objects[1];
			Integer ratedBy_id = (Integer) objects[2];
			Integer ratedBy_type_id = (Integer) objects[3];
			Integer target_id = (Integer) objects[4];
			Integer target_type_id = (Integer) objects[5];
			Float ratingVal = (Float) objects[6];
			hm.put("rate_id", rate_id);
			hm.put("comments", comments);
			hm.put("ratedBy_id", ratedBy_id);
			hm.put("ratedBy_type_id", ratedBy_type_id);
			hm.put("target_id", target_id);
			hm.put("target_type_id", target_type_id);
			hm.put("ratingVal", ratingVal);
			hmFinal.add(hm);
		}
		session.close();
		return hmFinal;

	}public static List findRatingByIdandType(int targetid, int targettypeid) {
		// creating seession factory object
		Session factory = HibernateUtil.buildSessionFactory();

		Session session = factory;

		// creating transaction object
		// Transaction trans = (Transaction) session.beginTransaction();

		Query query = session.createNativeQuery("SELECT `doctorsrating`.`rate_id`,\r\n"
				+ "    `doctorsrating`.`comments`,\r\n" + "    `doctorsrating`.`ratedBy_id`,\r\n"
				+ "    `doctorsrating`.`ratedBy_type_id`,\r\n" + "    `doctorsrating`.`target_id`,\r\n"
				+ "    `doctorsrating`.`target_type_id`,\r\n" + "    `doctorsrating`.`ratingVal`\r\n"
				+ " FROM doctorsrating where target_id=" + targetid + " and target_type_id=" + targettypeid + ";");
		List<Object[]> results = (List<Object[]>) query.getResultList();
		List hmFinal = new ArrayList();
		for (Object[] objects : results) {
			HashMap hm = new HashMap();
			Integer rate_id = (Integer) objects[0];
			String comments = (String) objects[1];
			Integer ratedBy_id = (Integer) objects[2];
			Integer ratedBy_type_id = (Integer) objects[3];
			Integer target_id = (Integer) objects[4];
			Integer target_type_id = (Integer) objects[5];
			Float ratingVal = (Float) objects[6];
			hm.put("rate_id", rate_id);
			hm.put("comments", comments);
			hm.put("ratedBy_id", ratedBy_id);
			hm.put("ratedBy_type_id", ratedBy_type_id);
			hm.put("target_id", target_id);
			hm.put("target_type_id", target_type_id);
			hm.put("ratingVal", ratingVal);
			hmFinal.add(hm);
		}
		session.close();
		return hmFinal;

	}

	public static Float findAverageRatingByIdandType(int targetid, int targettypeid) {
		// creating seession factory object
		Session factory = HibernateUtil.buildSessionFactory();

		Session session = factory;

		// creating transaction object
		// Transaction trans = (Transaction) session.beginTransaction();

		Query query = session.createNativeQuery("SELECT \r\n" + "    AVG(ratingVal) as ratingValAVG\r\n"
				+ " FROM doctorsrating where target_id=" + targetid + " and target_type_id=" + targettypeid + ";");

		String avg = query.getResultList().toString();
		avg = avg.replaceAll("[\\[\\]]", "");
		float docFrating = 0f;
		if ("null".equals(avg)) {
			return docFrating;
		} else {
			// avg = avg.replaceAll("[\\[\\]]", "");
			// list = list.replaceAll("]", " ");
			docFrating = Float.parseFloat(avg);

//		List<Object[]> results = (List<Object[]>) query.getResultList();
			// List hmFinal = new ArrayList();
			// HashMap hm = new HashMap();
//		for (Object[] objects : results) {
//			HashMap hm = new HashMap();
//			Double ratingValAVG = (Double) objects[0];
//			hm.put("ratingValAVG", ratingValAVG);
//			hmFinal.add(hm);
//		}
			// hm.put("ratingValAVG", docFrating);
			session.close();
		}
		return docFrating;

	}

	public static int updateRating(int targetId, int targetTypeId, int ratedById, int ratedByTypeId, float ratingVal,
			String comments) {

		// creating seession factory object
		Session factory = HibernateUtil.buildSessionFactory();

		// creating session object
		Session session = factory;
		// creating transaction object
		Transaction trans = (Transaction) session.beginTransaction();
		String updatestr = "";
		if ("null" != "" + ratingVal) {
			updatestr += "`ratingVal` = " + ratingVal + ",\r\n";
		}
		if ("null" != "" + comments) {
			updatestr += "`comments` = " + comments + ",\r\n";
		}

		updatestr = updatestr.replaceAll(",$", "");
		Query query = session.createNativeQuery("UPDATE `doctorsrating`\r\n" + " SET " + updatestr
				+ " WHERE `target_id` = " + targetId + " and `target_type_id` = " + targetTypeId
				+ " and `ratedBy_id` = " + ratedById + " and `ratedBy_type_id` = " + ratedByTypeId + ";");
		int ret = 0;
		try {
			ret = query.executeUpdate();
			trans.commit();
			System.out.println("updated article table for targetId =  " + targetId);

		} catch (Exception ex) {
			trans.rollback();
		} finally {
			// session.close();
			session.close();
		}
		// session.close();

		return ret;
	}

}