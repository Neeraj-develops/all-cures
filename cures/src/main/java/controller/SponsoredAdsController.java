package controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.SponsoredAdsDaoImpl;
@RestController
@RequestMapping(path = "/sponsored")
public class SponsoredAdsController {

	 // A map to store request counters for each date
    private Map<LocalDate, Integer> BannerCountMap = new ConcurrentHashMap<>();
    private Map<LocalDate, Integer> LeftCountMap = new ConcurrentHashMap<>();
    	private LocalDate lastRequestDate = null;
	@RequestMapping(value = "/create/company", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody Integer addcompanies(@RequestBody HashMap Company_Map,HttpServletRequest request ) throws Exception {

		return SponsoredAdsDaoImpl.InsertCompaniesDetails(Company_Map);
	}

	@RequestMapping(value = "/create/campaign", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody Integer addcampaign(@RequestBody HashMap Campaign_Map, HttpServletRequest request ) throws Exception {

		return SponsoredAdsDaoImpl.InsertCampaignDetails(Campaign_Map);
	}
	
	@RequestMapping(value = "/create/ad", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody Integer addcampaignsads(@RequestParam("image") CommonsMultipartFile image,
            @RequestParam("AdMap") String adMapJson,
            HttpServletRequest request) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		HashMap<String, Object> AdMap = objectMapper.readValue(adMapJson, new TypeReference<HashMap<String, Object>>() {});
		return SponsoredAdsDaoImpl.InsertAdDetails(AdMap, image);
	}
	@RequestMapping(value = "/add/stats", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody Integer addstats(@RequestBody HashMap StatsMap,HttpServletRequest request ) throws Exception {

		return SponsoredAdsDaoImpl.InsertAdStats(StatsMap);
	}
	@RequestMapping(value = "/all/companies", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List allcompanies(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.CompaniesDetails();
	}

	@RequestMapping(value = "/company/{CompanyID}", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List companiesID(HttpServletRequest request, @PathVariable int CompanyID ) throws Exception {

		return SponsoredAdsDaoImpl.CompaniesDetailsByID(CompanyID);
	}
	
	@RequestMapping(value = "/all/campaigns", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List allcampaigns(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.CampaignsDetails();
	}

	@RequestMapping(value = "/campaign/{CampaignID}", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List allcampaigns(HttpServletRequest request,@PathVariable int CampaignID ) throws Exception {

		return SponsoredAdsDaoImpl.CampaignsDetailsByID(CampaignID);
	}
	
	@RequestMapping(value = "/get/all/ads", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List allcampaignsads(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.CampaignsAds();
	}

	@RequestMapping(value = "/get/ads/{AdID}", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List campaignsadsID(HttpServletRequest request, @PathVariable int AdID) throws Exception {

		return SponsoredAdsDaoImpl.CampaignsAdsByID(AdID);
	}

	@RequestMapping(value = "/get/stats/{StatsID}", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List statsID(HttpServletRequest request, @PathVariable int StatsID) throws Exception {

		return SponsoredAdsDaoImpl.AdsStatsByID(StatsID);
	}
	@RequestMapping(value = "/update/company/{CompanyID}", produces = "application/json", method = RequestMethod.PUT)
	public @ResponseBody int updateCompany(@PathVariable int CompanyID, @RequestBody HashMap companyMap, HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.updateCompanyId(CompanyID, companyMap);
	
	
	}
	
	@RequestMapping(value = "/update/campaign/{CampaignID}", produces = "application/json", method = RequestMethod.PUT)
	public @ResponseBody int updateCampaign(@PathVariable int CampaignID, @RequestBody HashMap campaignMap, HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.updateCampaignId(CampaignID, campaignMap);
	
	
	}
	
	@RequestMapping(value = "/update/ad/{AdID}", produces = "application/json", method = RequestMethod.PUT)
	public @ResponseBody int updateCampaignAds(@PathVariable int AdID, @RequestBody HashMap campaignAdsMap, HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.updateCampaignAdsId(AdID, campaignAdsMap);
	
	
	}

	@RequestMapping(value = "/update/adstats/{StatsID}", produces = "application/json", method = RequestMethod.PUT)
	public @ResponseBody int updateAdsStats(@PathVariable int StatsID, @RequestBody HashMap StatsMap, HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.updateAdsStatsId(StatsID, StatsMap);
	
	}
	
	@RequestMapping(value = "/delete/campaign/{CampaignID}", produces = "application/json", method = RequestMethod.DELETE)
	public @ResponseBody int deleteCampaign(@PathVariable int CampaignID,  HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.deleteCampaignId(CampaignID);
	
	
	}
	
	@RequestMapping(value = "/delete/company/{CompanyID}", produces = "application/json", method = RequestMethod.DELETE)
	public @ResponseBody int deleteCompany(@PathVariable int CompanyID,  HttpServletRequest request) {
	
	return SponsoredAdsDaoImpl.deleteCompanyId(CompanyID);
	
	
	}

	@RequestMapping(value = "/list/companies", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List listcompanies(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.ListCompanies();
	}
	
	@RequestMapping(value = "/list/campaigns", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List listcampaigns(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.ListCampaigns();
	}
	

	@RequestMapping(value = "/list/adsslotstypes", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List listadsslots(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.ListAdsSlotsTypes();
	}
	
	@RequestMapping(value = "/list/adstargettypes", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List listadstarget(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.ListAdsTargetTypes();
	}
	
	@RequestMapping(value = "/list/adstypes", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List listadstypes(HttpServletRequest request) throws Exception {

		return SponsoredAdsDaoImpl.ListAdsTypes();
	}

	@RequestMapping(value = "/list/ads/url/{AdType}", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody String listadsURL(@PathVariable int AdType,HttpServletRequest request) throws Exception {
		LocalDate currentDate = LocalDate.now();
		String Res=null;
		if(AdType ==1)
		{
			  // Reset the request counter if a new day has started
	        if (lastRequestDate == null || !lastRequestDate.equals(currentDate)) {
	            BannerCountMap.clear();
	            lastRequestDate = currentDate;
	        }

	       System.out.println("Count : " + BannerCountMap.getOrDefault(currentDate, 0));
	       Res= SponsoredAdsDaoImpl.AdsURL(BannerCountMap,AdType);
	       request.setAttribute("customData", Res );
		}
		if(AdType ==2)
		{
			  // Reset the request counter if a new day has started
	        if (lastRequestDate == null || !lastRequestDate.equals(currentDate)) {
	            LeftCountMap.clear();
	            lastRequestDate = currentDate;
	        }
	       System.out.println("Count : " + LeftCountMap.getOrDefault(currentDate, 0));
	       Res= SponsoredAdsDaoImpl.AdsURL(LeftCountMap,AdType);
	       request.setAttribute("customData", Res );
		}
      
		return Res;
		
	}

	@RequestMapping(path="/ads/clicks/{adId}", produces = "application/json", method = RequestMethod.PUT )
	public int clickIncre(@PathVariable int  adId ) {
		return SponsoredAdsDaoImpl.clicksIncrement(adId);
	}
	
}
