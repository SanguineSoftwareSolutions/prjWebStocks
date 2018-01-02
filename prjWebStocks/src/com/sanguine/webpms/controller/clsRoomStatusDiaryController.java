package com.sanguine.webpms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.icu.util.Calendar;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.webpms.bean.clsRoomStatusDiaryBean;
import com.sanguine.webpms.bean.clsRoomStatusDtlBean;
import com.sanguine.webpms.service.clsRoomMasterService;

@Controller
public class clsRoomStatusDiaryController
{    
    @Autowired
    private clsRoomMasterService objRoomMasterService;
    
    @Autowired
    private clsGlobalFunctionsService objGlobalFunctionsService;
    
    @Autowired
    private clsGlobalFunctions objGlobal;
    
    
// Open Room Status Diary
    @RequestMapping(value = "/frmRoomStatusDiary", method = RequestMethod.GET)
    public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
    {
		String urlHits = "1";
		try
		{
		    urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
		    urlHits = "1";
		}
		model.put("urlHits", urlHits);
		
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		
		if (urlHits.equalsIgnoreCase("1"))
		{
		    return new ModelAndView("frmRoomStatusDiary", "command", new clsRoomStatusDiaryBean());
		}
		else
		{
		    return new ModelAndView("frmRoomStatusDiary_1", "command", new clsRoomStatusDiaryBean());
		}
    }
    
    
    
 // get Room Status Data
    @RequestMapping(value = "/getRoomStatusList", method = RequestMethod.GET)
    public @ResponseBody List funLoadRoomStatus(@RequestParam("viewDate") String viewDate, HttpServletRequest request)
    {
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		String userCode = request.getSession().getAttribute("usercode").toString();
		List listViewDates=new ArrayList();
		String[] arrViewDate=viewDate.split("-");
		GregorianCalendar cd=new GregorianCalendar();
		cd.set(Integer.parseInt(arrViewDate[2])-1900,Integer.parseInt(arrViewDate[1])-1,Integer.parseInt(arrViewDate[0]));
		
		Date dt=new Date(Integer.parseInt(arrViewDate[2])-1900,Integer.parseInt(arrViewDate[1])-1,Integer.parseInt(arrViewDate[0]));
		//System.out.println(dt.getDay());
		//System.out.println(dt);
		cd.setTime(dt);
		for(int cnt=0;cnt<7;cnt++)
		{
			String day=funGetDayOfWeek(cd.getTime().getDay());
			String transDate=(cd.getTime().getYear()+1900)+"-"+(cd.getTime().getMonth()+1)+"-"+cd.getTime().getDate();
			String date=day+" "+cd.getTime().getDate()+"-"+(cd.getTime().getMonth()+1)+"-"+(cd.getTime().getYear()+1900);
			System.out.println(date);
			listViewDates.add(date);
			cd.add(Calendar.DATE, 1);
		}
		
		System.out.println(listViewDates);
		return listViewDates;
    }
    
    
    
 // get Room Status Data
    @RequestMapping(value = "/getRoomStatusDtlList", method = RequestMethod.GET)
    public @ResponseBody List funLoadRoomStatusDetails(@RequestParam("viewDate") String viewDate, HttpServletRequest request)
    {
		String clientCode = request.getSession().getAttribute("clientCode").toString();
		String userCode = request.getSession().getAttribute("usercode").toString();
		String[] arrViewDate=viewDate.split("-");
		
		List<clsRoomStatusDtlBean> listRoomStatusBeanDtl=new ArrayList<clsRoomStatusDtlBean>();
		String sql="select a.strRoomCode,a.strRoomDesc,b.strRoomTypeDesc "
			+ " from tblroom a,tblroomtypemaster b "
			+ " where a.strRoomTypeCode=b.strRoomTypeCode";
		List listRoom=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
		
		for(int cnt1=0;cnt1<listRoom.size();cnt1++)
		{
			GregorianCalendar cd=new GregorianCalendar();
			cd.set(Integer.parseInt(arrViewDate[2])-1900,Integer.parseInt(arrViewDate[1])-1,Integer.parseInt(arrViewDate[0]));
			Date dt=new Date(Integer.parseInt(arrViewDate[2])-1900,Integer.parseInt(arrViewDate[1])-1,Integer.parseInt(arrViewDate[0]));
			cd.setTime(dt);
			
			Object[] arrObjRooms=(Object[])listRoom.get(cnt1);
			clsRoomStatusDtlBean objRoomStatusDtl=new clsRoomStatusDtlBean();
			objRoomStatusDtl.setStrRoomNo(arrObjRooms[1].toString());
			objRoomStatusDtl.setStrRoomType(arrObjRooms[2].toString());
			
			for(int cnt=0;cnt<7;cnt++)
			{
				String day=funGetDayOfWeek(cd.getTime().getDay());
				String transDate=(cd.getTime().getYear()+1900)+"-"+(cd.getTime().getMonth()+1)+"-"+cd.getTime().getDate();
				String date=day+" "+cd.getTime().getDate()+"-"+(cd.getTime().getMonth()+1)+"-"+(cd.getTime().getYear()+1900);
				//System.out.println(date);
				cd.add(Calendar.DATE, 1);
							
				sql="select a.strReservationNo,d.strRoomCode,d.strRoomDesc"
					+ " ,concat(c.strFirstName,' ',c.strMiddleName,' ',c.strLastName),e.strBookingTypeDesc,DATE_FORMAT(DATE(a.dteArrivalDate),'%d-%m-%Y'), DATE_FORMAT(DATE(a.dteDepartureDate),'%d-%m-%Y') "
					+ " from tblreservationhd a,tblreservationdtl b,tblguestmaster c,tblroom d ,tblbookingtype e "
					+ " where a.strReservationNo=b.strReservationNo and b.strGuestCode=c.strGuestCode "
					+ " and b.strRoomNo=d.strRoomCode and a.strBookingTypeCode=e.strBookingTypeCode "
					+ " and date(a.dteDepartureDate) >= '"+transDate+"' and date(a.dteArrivalDate) <= '"+transDate+"' "
					+ " and b.strRoomNo='"+arrObjRooms[0].toString()+"' "
					+ " and a.strReservationNo not in (select strReservationNo from tblcheckinhd) ";
				List listRoomDtl=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
				if(listRoomDtl.size()>0)
				{
					Object[] arrObjRoomDtl=(Object[])listRoomDtl.get(0);
					objRoomStatusDtl.setStrReservationNo(arrObjRoomDtl[0].toString());
					objRoomStatusDtl.setStrGuestName(arrObjRoomDtl[3].toString());
					objRoomStatusDtl.setStrRoomStatus(arrObjRoomDtl[4].toString());
					
					objRoomStatusDtl.setDteArrivalDate(arrObjRoomDtl[5].toString());
					objRoomStatusDtl.setDteDepartureDate(arrObjRoomDtl[6].toString());
					
					if(cnt==0)
					{
						objRoomStatusDtl.setStrDay1(arrObjRoomDtl[3].toString());
					}
					else if(cnt==1)
					{
						objRoomStatusDtl.setStrDay2(arrObjRoomDtl[3].toString());
					}
					else if(cnt==2)
					{
						objRoomStatusDtl.setStrDay3(arrObjRoomDtl[3].toString());
					}
					else if(cnt==3)
					{
						objRoomStatusDtl.setStrDay4(arrObjRoomDtl[3].toString());
					}
					else if(cnt==4)
					{
						objRoomStatusDtl.setStrDay5(arrObjRoomDtl[3].toString());
					}
					else if(cnt==5)
					{
						objRoomStatusDtl.setStrDay6(arrObjRoomDtl[3].toString());
					}
					else if(cnt==6)
					{
						objRoomStatusDtl.setStrDay7(arrObjRoomDtl[3].toString());
					}
				}
				else
				{
					sql="select a.strReservationNo,d.strRoomCode,d.strRoomDesc"
						+ " ,concat(c.strFirstName,' ',c.strMiddleName,' ',c.strLastName),'Occupied',DATE_FORMAT(DATE(a.dteArrivalDate),'%d-%m-%Y'), DATE_FORMAT(DATE(a.dteDepartureDate),'%d-%m-%Y')"
						+ " ,a.strCheckInNo "
						+ " from tblcheckinhd a,tblcheckindtl b,tblguestmaster c,tblroom d "
						+ " where a.strCheckInNo=b.strCheckInNo and b.strGuestCode=c.strGuestCode and b.strRoomNo=d.strRoomCode "
						+ " and date(a.dteDepartureDate) >= '"+transDate+"' and date(a.dteArrivalDate) <= '"+transDate+"' "
						+ " and b.strRoomNo='"+arrObjRooms[0].toString()+"' "
						+ " and a.strCheckInNo not in (select strCheckInNo from tblbillhd) ";
					List listCheckInRoomDtl=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
					if(listCheckInRoomDtl.size()>0)
					{
						Object[] arrObjRoomDtl=(Object[])listCheckInRoomDtl.get(0);
						objRoomStatusDtl.setStrReservationNo(arrObjRoomDtl[0].toString());
						objRoomStatusDtl.setStrGuestName(arrObjRoomDtl[3].toString());
						objRoomStatusDtl.setStrRoomStatus(arrObjRoomDtl[4].toString());
						
						objRoomStatusDtl.setDteArrivalDate(arrObjRoomDtl[5].toString());
						objRoomStatusDtl.setDteDepartureDate(arrObjRoomDtl[6].toString());
						objRoomStatusDtl.setStrCheckInNo(arrObjRoomDtl[7].toString());
						
						if(cnt==0)
						{
							objRoomStatusDtl.setStrDay1(arrObjRoomDtl[3].toString());
						}
						else if(cnt==1)
						{
							objRoomStatusDtl.setStrDay2(arrObjRoomDtl[3].toString());
						}
						else if(cnt==2)
						{
							objRoomStatusDtl.setStrDay3(arrObjRoomDtl[3].toString());
						}
						else if(cnt==3)
						{
							objRoomStatusDtl.setStrDay4(arrObjRoomDtl[3].toString());
						}
						else if(cnt==4)
						{
							objRoomStatusDtl.setStrDay5(arrObjRoomDtl[3].toString());
						}
						else if(cnt==5)
						{
							objRoomStatusDtl.setStrDay6(arrObjRoomDtl[3].toString());
						}
						else if(cnt==6)
						{
							objRoomStatusDtl.setStrDay7(arrObjRoomDtl[3].toString());
						}
					}
				}
				
				sql="select a.strReservationNo,f.strRoomCode,f.strRoomDesc,concat(e.strFirstName,' ',e.strMiddleName,' ',e.strLastName)"
					+ " ,'Checked Out',DATE_FORMAT(DATE(c.dteArrivalDate),'%d-%m-%Y'), DATE_FORMAT(DATE(c.dteDepartureDate),'%d-%m-%Y')"
					+ ",a.strCheckInNo "
					+ " from tblbillhd a,tblcheckinhd c,tblcheckindtl d,tblguestmaster e,tblroom f "
					+ " where a.strCheckInNo=c.strCheckInNo and c.strCheckInNo=d.strCheckInNo and d.strGuestCode=e.strGuestCode "
					+ " and a.strRoomNo=f.strRoomCode and date(a.dteBillDate) >= '"+transDate+"' and date(c.dteArrivalDate) <= '"+transDate+"' " 
					+ " and a.strRoomNo='"+arrObjRooms[0].toString()+"'";
				List listCheckOutRoomDtl=objGlobalFunctionsService.funGetListModuleWise(sql, "sql");
				if(listCheckOutRoomDtl.size()>0)
				{
					Object[] arrObjRoomDtl=(Object[])listCheckOutRoomDtl.get(0);
					objRoomStatusDtl.setStrReservationNo(arrObjRoomDtl[0].toString());
					objRoomStatusDtl.setStrGuestName(arrObjRoomDtl[3].toString());
					objRoomStatusDtl.setStrRoomStatus(arrObjRoomDtl[4].toString());
					
					objRoomStatusDtl.setDteArrivalDate(arrObjRoomDtl[5].toString());
					objRoomStatusDtl.setDteDepartureDate(arrObjRoomDtl[6].toString());
					objRoomStatusDtl.setStrCheckInNo(arrObjRoomDtl[7].toString());
					
					if(cnt==0)
					{
						objRoomStatusDtl.setStrDay1(arrObjRoomDtl[3].toString());
					}
					else if(cnt==1)
					{
						objRoomStatusDtl.setStrDay2(arrObjRoomDtl[3].toString());
					}
					else if(cnt==2)
					{
						objRoomStatusDtl.setStrDay3(arrObjRoomDtl[3].toString());
					}
					else if(cnt==3)
					{
						objRoomStatusDtl.setStrDay4(arrObjRoomDtl[3].toString());
					}
					else if(cnt==4)
					{
						objRoomStatusDtl.setStrDay5(arrObjRoomDtl[3].toString());
					}
					else if(cnt==5)
					{
						objRoomStatusDtl.setStrDay6(arrObjRoomDtl[3].toString());
					}
					else if(cnt==6)
					{
						objRoomStatusDtl.setStrDay7(arrObjRoomDtl[3].toString());
					}
				}
			}
			
			listRoomStatusBeanDtl.add(objRoomStatusDtl);
		}
		
		System.out.println(listRoomStatusBeanDtl);
		return listRoomStatusBeanDtl;
    }
    
    
    
    private String funGetDayOfWeek(int day)
    {
    	String dayOfWeek= "Sun";
    	
    	switch(day)
    	{
    		case 0:
    			dayOfWeek="Sun";
    			break;
    			
    		case 1:
    			dayOfWeek="Mon";
    			break;
    			
    		case 2:
    			dayOfWeek="Tue";
    			break;
    		
    		case 3:
    			dayOfWeek="Wed";
    			break;
    			
    		case 4:
    			dayOfWeek="Thur";
    			break;
    			
    		case 5:
    			dayOfWeek="Fri";
    			break;
    			
    		case 6:
    			dayOfWeek="Sat";
    			break;
    	}
    	
    	return dayOfWeek;
    }
}
