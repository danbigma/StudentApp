package com.studentapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Years;

import com.mysql.cj.log.Log;
import com.mysql.cj.log.LogFactory;



public class DateUtils {
	   
    public static final String ENERO = "01";
    public static final String FEBRERO = "02";
    public static final String MARZO = "03";
    public static final String ABRIL = "04";
    public static final String MAYO = "05";
    public static final String JUNIO = "06";
    public static final String JULIO = "07";
    public static final String AGOSTO = "08";
    public static final String SEPTIEMBRE = "09";
    public static final String OCTUBRE = "10";
    public static final String NOVIEMBRE = "11";
    public static final String DICIEMBRE = "12";

    private static final String className = DateUtils.class.getName();
    
//    private static Log log = LogFactory.getLog(className);

    private static final long UN_DIA_EN_MILISEGUNDOS = 86400000;

    private DateUtils(){}

    /**
     * metodo que asegura que la fecha devuelta sea un Date
     * La fecha no es truncada (se devuelve con hh:mm:ss)
     * @param fecha
     * @return
     */
    public static Date convertirFecha(Date fecha) {

        if (fecha != null) {
            try { //castear a DATE no vale que sea TIMESTAMP
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fecha);
                return calendar.getTime();
            } catch (RuntimeException e) {
//               log.debug("Error ", e);
            }
        }
        return null;
    }

    public static Integer getAnno(Date date) {
        return getDateField(date, Calendar.YEAR);
    }

    public static Integer getMes(Date date) {
        return getDateField(date, Calendar.MONTH);
    }

    public static Integer getDiaDelMes(Date date) {
        return getDateField(date, Calendar.DAY_OF_MONTH);
    }

    private static Integer getDateField(Date date, int field) {
        if (date == null) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    public static String dateFormatMedium(Date date) {

        if (date == null) {
            return "null";
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return df.format(date);
    }

    /**
     * Deja la fecha solo con año, mes y dia, los horas min y seg los resetea.
     *
     * @param date
     * @return
     */
    public static Date truncateDate(Date date) {

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH),
                getDateField(date, Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * Devuelve una fecha igual a la fecha que recibe como parametro
     * pero ajustando los segundos a 1
     */
    public static Date getFechaParaEventoAutomatico(Date date) {

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH),
                getDateField(date, Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.SECOND, 1);

        return calendar.getTime();
    }

    /**
     * Devuelve una fecha igual a la fecha que recibe como parametro
     * pero con la fecha, minutos y segundos actuales
     */
    public static Date getFechaParaEventoManual(Date date) {

        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH),
                getDateField(date, Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }


    public static String formatDateTime(Date date) {

        if (date == null) {
            return "null";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }

    public static String formatShortDate(Date date) {

        if (date == null) {
            return "null";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatDateInvers(Date date) {

        if (date == null) {
            return "null";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public static Long formatDate(Date date){
        if (date == null) {
            return Long.valueOf(0);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        return new Long(sdf.format(date));
    }


    public static String formatDateWithDots(Date date){
        if (date == null){
            return "";
        } else {
             SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
             return sdf.format(date);
        }
    }


    public static String formatDateConGuionesInversa(Date date){
        if (date==null){
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }


    public static String formatDateConGuiones(Date date){
    	
        if (date==null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);
        }
    }
    
    public static String formatDateConGuionesMesCorto(Date date){
        if (date==null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            return sdf.format(date);
        }
    }
    
    public static String formatDateConBarra(Date date){
        if (date==null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(date);
        }
    }
    
    public static String formatDateConLetra(Date date){
        if (date==null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
            String fechaBarras = sdf.format(date);
            String fechaLetras = fechaBarras.split("/")[0] + " of " + fechaBarras.split("/")[1] + " of " + fechaBarras.split("/")[2];
            
            return fechaLetras;
        }
    }
    
//    public static String formatDateTexto(Date date) {
//        if (date==null) {
//            return "";
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
//            String fechaBarras = sdf.format(date);
//            NumberToText n2t = new NumberToText();
//            
//            String fechaTexto = n2t.convertirLetras(Integer.parseInt(fechaBarras.split("/")[0])) + " de " + fechaBarras.split("/")[1] + " de " + n2t.convertirLetras(Integer.parseInt(fechaBarras.split("/")[2]));
//            
//            return fechaTexto;
//        }
//    }
    
    public static Date restaDia(Date date) {

        if (date == null) {
            return date;
        }
        
        return restaUnDiaCalendario(date);
    }

    public static Date restaDosDias(Date date) {

        if (date == null) {
            return date;
        }
        return restaUnDiaCalendario(restaUnDiaCalendario(date));
    }

    public static Date sumaUnDiaCalendario(Date fechaInicial) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.DAY_OF_MONTH, 1);
            return cal.getTime();
    }

    public static Date restaUnDiaCalendario(Date fechaInicial) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.DAY_OF_MONTH, -1);
            return cal.getTime();
    }

    public static Date sumarAnyos(Date fecha, Integer anyos) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.YEAR, anyos);
            return cal.getTime();
    }

    //suma a la fecha indicada el numero de mes indicados por parametro
    public static Date p(Date fechaInicial, Integer meses) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.MONTH, meses);
            return cal.getTime();
    }

    public static Date sumarDias(Date fechaInicial, Integer dias) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.DAY_OF_MONTH, dias);
            return cal.getTime();
    }


    public static Date sumarMeses(Date fechaInicial, Integer meses) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.MONTH, meses);
            return cal.getTime();
    }


    public static Date restarDias(Date fechaInicial, Integer dias) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicial);
        cal.add(Calendar.DAY_OF_MONTH, -dias);
            return cal.getTime();
    }

    /**
     * Deja la fecha solo con año, mes y dia, aplicando el fin de mes a la fecha parametro
     *
     * @param date
     * @return
     */
    public static Date llevarFinMes(Date date) {


        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH),
                getDateField(date, Calendar.DAY_OF_MONTH));


        Integer ultimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);

        return calendar.getTime();
    }
    
    public static Date obtenerDiaHabil(Date date) {
        
        Date fechaResultado=null;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH), getDateField(date, Calendar.DAY_OF_MONTH));
        Integer diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
        
        if ( diaSemana==7 || diaSemana==1 ) { //sabado o domingo
            if (isFinMes(date)) { //es el ultimo dia del mes, nos vamos al viernes, restamos dos dias
                if(diaSemana==7) { //es sabado resto un dia
                    fechaResultado=DateUtils.restaUnDiaCalendario(date);
                }if(diaSemana==1) { //es domingo resto dos
                    fechaResultado=DateUtils.restaDosDias(date);
                }
            }else { //si no es fin de mes, nos vamos al lunes siguiente (sumamos un dia)
                if(diaSemana==7) { //sumar dos dias
                    fechaResultado=DateUtils.sumarDias(date, 2);
                }if(diaSemana==1) { //es domingo sumar 1 dias
                    fechaResultado=DateUtils.sumarDias(date, 1);
                }               
            }           
        }  
        
        return fechaResultado;
        
    }
    
    public static boolean isFinMes(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(getDateField(date, Calendar.YEAR),
                getDateField(date, Calendar.MONTH), getDateField(date, Calendar.DAY_OF_MONTH));

        Integer ultimoDiaMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
        Date fechaFinMes=calendar.getTime();
        Double dias=restarFechas(fechaFinMes,date);
        
        if(dias==0){
            return true;
        }else {
            return false;
        }
        
    }

    /**
     * Deja las fechas solo con año, mes y dia, aplicando el fin de mes a las fechas parametro
     *
     * @param listaFechas
     * @return
     */
    public static List<Date> llevarFinMes(List<Date> listaFechas) {

        List<Date> listaRetorno = new ArrayList<Date>();
        for(int x=0; x<listaFechas.size();x++){
            listaRetorno.add(llevarFinMes(listaFechas.get(x)));
        }
        if(listaRetorno.size()>0) Collections.sort(listaRetorno);

        return listaRetorno;
    }
    
    
    
    
    

    /**
     * Retorna diferencia de dias entre fechas
     *
     * @param fecha1
     * @param fecha2
     *
     * @return Numero (con signo) con la diferencia de dias entre fechas
     */
    public static int daysDifference(Date fecha1, Date fecha2) {
        long diff = 0;

        Calendar calendarA = GregorianCalendar.getInstance();
        Calendar calendarB = Calendar.getInstance();
        int multiplier = 0;
        if (fecha2.getTime() - fecha1.getTime() > 0) {
            multiplier = -1;
            calendarA.setTime(fecha2);
            calendarB.setTime(fecha1);
        } else {
            multiplier = 1;
            calendarA.setTime(fecha1);
            calendarB.setTime(fecha2);
        }

        diff = difDiasEntre2fechas(calendarB.get(Calendar.YEAR), calendarB
                .get(Calendar.MONTH) + 1, calendarB.get(Calendar.DAY_OF_MONTH),
                calendarA.get(Calendar.YEAR),
                calendarA.get(Calendar.MONTH) + 1, calendarA
                        .get(Calendar.DAY_OF_MONTH));

        // Conversión de dias
        Long dias = Long.valueOf(diff);

        return dias.intValue() * multiplier;
    }

    /*
     * Método refactorizado de la clase AplicacionDiasHabiles
     */
    private static long difDiasEntre2fechas(int y1, int m1, int d1, int y2,
            int m2, int d2) {
        DateTimeZone dateTimeZoneMadrid = DateTimeZone.forID("Europe/Madrid");
        DateTime dt2 = new DateTime(y1, m1, d1, 0, 0, 0, 0, dateTimeZoneMadrid);
        DateTime dt1 = new DateTime(y2, m2, d2, 0, 0, 0, 0, dateTimeZoneMadrid);
        return Days.daysBetween(dt2, dt1).getDays();
    }

    /*
     * Convierte una fecha q se le pasa como parametro de tipo String
     * a un Date
     */
    public static Date convertirFecha(String fecha){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");	
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }
    
    
    public static Date convertirFechaGuiones(String fecha) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }


    /*
     * Convierte una fecha q se le pasa como parametro de tipo String
     * a un Date
     */
    public static Date convertirFechaConGuiones(String fecha){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }


    public static Date convertirFechaYYYYMMdd (String fecha){
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }
    
    public static Date convertirFechaDDMMMyy (String fecha){
        
        DateFormat df = new SimpleDateFormat("dd-MMM-yy");
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }

    public static Date convertirFecha_YYYYMMdd (String fecha){
        
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try
        {
            return df.parse(fecha);

        } catch (ParseException e)
        {
           return null;
        }
    }
    
    /**
     * Método que devuelve la fecha actual del sistema.
     *
     * @return
     */
//    public static Date getFechaSistema(){
//        return FechaSistemaSingleton.getInstance().getFechaSistema();
//    }


    /**
     * Método devuelve true si la fecha es un día festivo
     *
     * @return
     */
    public static boolean isFestivo(List<Date> festivos, Date fecha ){
        boolean isFestivo = false;
        for(Date festivo : festivos) {
            if(festivo.compareTo(DateUtils.truncateDate(fecha))==0) {
                isFestivo = true;
                break;
            }
            else {
                if(festivo.after(DateUtils.truncateDate(fecha))) {
                    break;
                }
            }
        }

        return isFestivo;
    }

    public static String secondsToString(long time){
          int seconds = (int)(time % 60);
          int minutes = (int)((time/60) % 60);
          int hours = (int)((time/3600) % 24);
          String secondsStr = (seconds<10 ? "0" : "")+ seconds;
          String minutesStr = (minutes<10 ? "0" : "")+ minutes;
          String hoursStr = (hours<10 ? "0" : "")+ hours;
          return new String(hoursStr + ":" + minutesStr + ":" + secondsStr);
   }

    public static XMLGregorianCalendar convertirXMLGregorianCalendar(Date date)  {
        
        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        gc.setTime(date);
        DatatypeFactory dataTypeFactory = null;
        try {
        dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            return null;
        }
        
        XMLGregorianCalendar value = dataTypeFactory.newXMLGregorianCalendar(gc);
        return value;
    }
    
    // Convierte Fecha Tipo GregorianCalendar a Tipo Date
    public static Date convertirdXMLLGregorianCalendarDate(XMLGregorianCalendar date)  {    
        return date.toGregorianCalendar().getTime();
    }
    
    public static boolean isFinDeSemana(Date fecha) {
        boolean finDeSemana = false;

        if (fecha != null) {
            Calendar calendario = new GregorianCalendar();
            calendario.setTime(fecha);

            if (calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    || calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                finDeSemana = true;
            }
        }

        return finDeSemana;
    }
    
    //INI - ICO-36896 - 08-06-2015
    public static XMLGregorianCalendar getXMLCalendar_2(String strDate, String format) throws Exception {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar calendar = new GregorianCalendar();
        // reset all fields
        calendar.clear();

        Calendar parsedCalendar = Calendar.getInstance();
        // eg "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        // this may throw ParseException
        Date rawDate = sdf.parse( strDate );
        parsedCalendar.setTime( rawDate );

        // set date from parameter and leave time as default calendar values
        calendar.set( parsedCalendar.get( Calendar.YEAR ), 
                        parsedCalendar.get( Calendar.MONTH ),
                        parsedCalendar.get( Calendar.DATE ) );

        XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar( calendar );
        // clears default timezone
        xmlCalendar.setTimezone( DatatypeConstants.FIELD_UNDEFINED );
      


        return xmlCalendar;
    }
    //FIN - ICO-36896
    
    public static XMLGregorianCalendar getXMLCalendar(String strDate) throws Exception {
        
        Calendar sDate = Calendar.getInstance();
        DatatypeFactory dtf = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = null;
        
        // Dates (CCYY-MM-DD)
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-M-d");
        
        if(strDate != null)
        {
            sDate.setTime(DATE_FORMAT.parse(strDate));
            calendar = dtf.newXMLGregorianCalendar();
            calendar.setYear(sDate.get(Calendar.YEAR));
            calendar.setDay(sDate.get(Calendar.DAY_OF_MONTH));
            calendar.setMonth(sDate.get(Calendar.MONTH)+ 1);
        }
        
        return calendar;
    }
    
    
    public static double restarFechas(Date fechaInicio, Date fechaFin){
        
        GregorianCalendar fInicio = new GregorianCalendar();
        fInicio.setTime(fechaInicio);
        GregorianCalendar fFin = new GregorianCalendar();
        fFin.setTime(fechaFin);
        //Le sumamos 1 al mes porque en GregorianCalendar en numero de mes empieza por 0, pero la clase DateTime el mes empieza por 1
        DateTime start = new DateTime(fInicio.get(Calendar.YEAR), fInicio.get(Calendar.MONTH) + 1, fInicio.get(Calendar.DATE), 0, 0, 0, 0);
        DateTime end = new DateTime(fFin.get(Calendar.YEAR), fFin.get(Calendar.MONTH) + 1, fFin.get(Calendar.DATE), 0, 0, 0, 0);    
        
        //calcula el número de dias entre 2 fechas
        Days days = Days.daysBetween(start, end);
        
        return days.getDays();
    }
    
    /**
     * Devuelve el número de días de diferencia entre dos fechas.
     * @param fechaInicio
     * @param fechaFin
     * @return
     */
    public static int restarFechasEnAnyos(Date fechaInicio, Date fechaFin){
        
        GregorianCalendar fInicio = new GregorianCalendar();
        fInicio.setTime(fechaInicio);
        GregorianCalendar fFin = new GregorianCalendar();
        fFin.setTime(fechaFin);
        //Le sumamos 1 al mes porque en GregorianCalendar en numero de mes empieza por 0, pero la clase DateTime el mes empieza por 1
        DateTime start = new DateTime(fInicio.get(Calendar.YEAR), fInicio.get(Calendar.MONTH) + 1, fInicio.get(Calendar.DATE), 0, 0, 0, 0);
        DateTime end = new DateTime(fFin.get(Calendar.YEAR), fFin.get(Calendar.MONTH) + 1, fFin.get(Calendar.DATE), 0, 0, 0, 0);    
        
        //calcula el número de dias entre 2 fechas
        Years years = Years.yearsBetween(start, end);
        
        return years.getYears();
    }
    
    /**
     * formateo de fechas que vienen con el formato Host YYYYMMDD a tipo Date
     * @param fecha String
     * @return Date
     */
    public  static Date formatFechaInversa(String fecha){
        
        //formato de entrada host YYYYMMDD -> dd/MM/yyyy
        String cadenaFecha = fecha.substring(6, 8)+
        "/" + fecha.substring(4, 6) + "/" + fecha.substring(0, 4);
        
        return convertirFecha(cadenaFecha);
        
        
    }
    
    /**
     * 
     * Dada una fecha en formato YYYYMM (String) nos devuelve el último día del mes en cuestión, teniendo en cuenta si es bisiesto o no.
     * 
     * @param String fecha
     * @return String
     */
    public static String obtenerUltimoDiaDelMes(String fecha){
        
        //formato de entrada: YYYYMMDD
        
        String mes = fecha.substring(4, 6);
        String ultimoDiaMes = "";
        
        if(mes.equals(FEBRERO)){
            
            //comprobar si es bisiesto
            GregorianCalendar calendar = new GregorianCalendar();
            Integer anio = Integer.valueOf(fecha.substring(0, 4));

            if (calendar.isLeapYear(anio.intValue()))
                ultimoDiaMes = "29";
            else
                ultimoDiaMes = "28";
            
        }else if (mes.equals(ABRIL) || mes.equals(JUNIO) || mes.equals(SEPTIEMBRE) || mes.equals(NOVIEMBRE)){
            //meses de 30 días
            ultimoDiaMes = "30";
            
        }else{
            //meses de 31 días
            ultimoDiaMes = "31";
        }
        
        
        return ultimoDiaMes;
    }
    
    /**
     * Dada una fecha y la lista de festivos, nos devuelve el siguiente día hábil en caso de que sea festivo o fin de semana
     * @param fechaComunicado
     * @param diasFestivos
     * @return Date
     */
    public static Date calcularSiguienteFechaValida(Date fechaComunicado, List<Date> diasFestivos) {
            
            Date fechaValida = fechaComunicado;
            
            while (DateUtils.isFestivo(diasFestivos, fechaValida) || DateUtils.isFinDeSemana(fechaValida)){
                
                //Actualizar la fecha dada en un día más
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaValida);
                cal.add(Calendar.DATE, 1);
                fechaValida = cal.getTime();
                
            }
        
        return fechaValida;
    }
    
    
    /**
     * Valida si la fecha es correcta.
     * @param fecha
     * @return
     */
    public static boolean isValidaFecha(String fecha) {  
          
        if (fecha == null)  
            return false;  

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      
        if (fecha.trim().length() != dateFormat.toPattern().length())  
            return false;  
      
        dateFormat.setLenient(false);  
      
        try {  
            dateFormat.parse(fecha.trim());  
        }  
        catch (Exception pe) {  
            return false;  
        }  
        return true;  
    }

    /**
     * Valida si la fecha es correcta dado un formato.
     * @param fecha
     * @return
     */
    public static boolean isValidaFecha(String fecha, SimpleDateFormat dateFormat) {  
          
        if (fecha == null)  
            return false;  

      
        if (fecha.trim().length() != dateFormat.toPattern().length())  
            return false;  
      
        dateFormat.setLenient(false);  
      
        try {  
            dateFormat.parse(fecha.trim());  
        }  
        catch (Exception pe) {  
            return false;  
        }  
        return true;  
    }
    /**
     * Resta los el número de meses indicado a la fecha pasada por parámetro
     * @param fechaVencimiento
     * @param numMeses
     * @return Date
     */
    public static Date restaMeses(Date fechaVencimiento, int numMeses) {

        Date fechaInicioPeriodo = null;
        Calendar cFecha = GregorianCalendar.getInstance();
        cFecha.setTime(fechaVencimiento);
        cFecha.add(Calendar.MONTH, -(numMeses));
        
        fechaInicioPeriodo = cFecha.getTime();
        fechaInicioPeriodo = DateUtils.llevarFinMes(fechaInicioPeriodo);
        
        cFecha.setTime(fechaInicioPeriodo);
        cFecha.add(Calendar.DAY_OF_MONTH, 1);
        
        fechaInicioPeriodo = cFecha.getTime();
        
        return fechaInicioPeriodo;
    }  
    
    public static Integer getMesesDiferencia(Calendar beginningDate, Calendar endingDate) {
       
        if (beginningDate == null || endingDate == null) {
            return 0;
        }
        int m1 = beginningDate.get(Calendar.YEAR) * 12 + beginningDate.get(Calendar.MONTH);
        int m2 = endingDate.get(Calendar.YEAR) * 12 + endingDate.get(Calendar.MONTH);
        return m2 - m1;
    }
    
    public static Integer getMesesDiferencia(Date beginningDate, Date endingDate) {
        if (beginningDate == null || endingDate == null) {
            return 0;
        }
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(beginningDate);
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(endingDate);
        
        return getMesesDiferencia(cal1, cal2);
    }
    
    

    

    public static Calendar DateToCalendar(Date date){ 
          Calendar cal = Calendar.getInstance();
          cal.setTime(date);
          return cal;
        }

    
//    public static Date obtenerDateFromString(String dateString) {
//
//        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
//        String strFecha = dateString;
//        Date fechaDate = null;
//
//        try {
//
//            fechaDate = formato.parse(strFecha);
//            return fechaDate;
//
//        } catch (Exception e) {
//            log.error(className + "obtenerDateFromString(..):" + e.getMessage(),e);
//            return fechaDate;
//        }
//
//    }

    public static boolean fechaEnAnoBisiesto(Date fecha) {    
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fecha);
        
        return calendar.isLeapYear(calendar.get(Calendar.YEAR));        
    }
    
    public static Date restarDosDiasHabiles(Date fecha)
    {
          Date fechaResultado=null;
          Calendar calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(getDateField(fecha, Calendar.YEAR),
                  getDateField(fecha, Calendar.MONTH), getDateField(fecha, Calendar.DAY_OF_MONTH));
          Integer diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
          
                if(getDateField(fecha, Calendar.MONTH)==11&&getDateField(fecha, Calendar.DAY_OF_MONTH)==31)
                {
                    fechaResultado=fecha;
                }
                else
                {
                    if(diaSemana==1||diaSemana==2||diaSemana==3) { //si es domingo(1), lunes o martes le restamos 4 dias
                        fechaResultado=DateUtils.restarDias(fecha, 4);
                    }
                    else if(diaSemana==7) { //es sabado resto 3 dias
                        fechaResultado=DateUtils.restarDias(fecha, 3);
                    }
                    else
                    {
                        fechaResultado=DateUtils.restarDias(fecha, 2);
                    }
                }
          
          return fechaResultado;
    }
}
