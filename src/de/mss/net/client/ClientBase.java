package de.mss.net.client;

import java.util.ArrayList;
import java.util.List;

import de.mss.configtools.ConfigFile;
import de.mss.net.Server;
import de.mss.net.rest.RestServer;
import de.mss.utils.exception.MssException;

public abstract class ClientBase {

   protected RestServer[] servers;
   protected ConfigFile   cfg;
   protected String       cfgBaseKey;

   public ClientBase(ConfigFile c, String ck) throws MssException {
      this.cfg = c;
      this.cfgBaseKey = ck;
      initServers();
   }


   private void initServers() throws MssException {
      if (this.cfg.contains(this.cfgBaseKey + ".urls")) {
         initServersFromUrls();
      } else {
         initServersFromValues();
      }
   }


   private void initServersFromUrls() throws MssException {
      final String[] urls = this.cfg.getValue(this.cfgBaseKey + ".urls", "").split(";");
      final List<RestServer> s = new ArrayList<>();
      for (final String url : urls) {
         s.add(new RestServer(url));
      }
      this.servers = s.toArray(new RestServer[s.size()]);
   }


   private void initServersFromValues() throws MssException {
      final int maxCount = this.cfg.getValue(this.cfgBaseKey + ".numberOfServers", 1);
      final List<RestServer> s = new ArrayList<>();

      for (int i = 1; i <= maxCount; i++ ) {
         final RestServer rs = new RestServer(
               new Server(
                     this.cfg.getValue(this.cfgBaseKey + ".protocoll" + i, "http"),
                     this.cfg.getValue(this.cfgBaseKey + ".url" + i, "localhost"),
                     this.cfg.getValue(this.cfgBaseKey + ".port" + i, 8080),
                     this.cfg.getValue(this.cfgBaseKey + ".path" + i, "")));
         s.add(rs);
      }

      this.servers = s.toArray(new RestServer[s.size()]);
   }
}
