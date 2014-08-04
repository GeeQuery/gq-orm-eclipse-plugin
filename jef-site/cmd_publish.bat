@set site_path=E:\DevDocuments\nexus-2.4.0-09-bundle\nexus-2.4.0-09\nexus\jef-plugin\
@xcopy /e /s /y features %site_path%features\
@xcopy /e /s /y plugins %site_path%plugins\
@copy /y artifacts.jar %site_path%
@copy /y content.jar %site_path%
@copy /y index.html %site_path%
call cmd_clean