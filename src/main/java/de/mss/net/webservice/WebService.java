package de.mss.net.webservice;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import de.mss.configtools.ConfigFile;
import de.mss.utils.exception.MssException;

public interface WebService {

   public boolean handleRequest(
         String loggingId,
         String target,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse);


   public
         int
         get(String loggingId, Map<String, String> params, Request baseRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
               throws MssException;


   public int post(
         String loggingId,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException;


   public int patch(
         String loggingId,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException;


   public int delete(
         String loggingId,
         Map<String, String> params,
         Request baseRequest,
         HttpServletRequest httpRequest,
         HttpServletResponse httpResponse)
         throws MssException;


   public String getPath();


   public void setConfig(ConfigFile c);
}
