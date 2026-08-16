package sap.commerce.cx.occ.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.hybris.platform.b2bocc.v2.controllers.BaseController;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import jakarta.annotation.Resource;
import sap.commerce.cx.facades.dashboard.CxDashboardFacade;
import sap.commerce.cx.facades.data.dashboard.CxDashboardConfigData;
import sap.commerce.cx.facades.data.dashboard.CxDashboardWidgetData;
import sap.commerce.cx.occ.dto.dashboard.CxDashboardConfigWsDTO;
import sap.commerce.cx.occ.dto.dashboard.CxDashboardWidgetWsDTO;

@RestController
@Tag(name = "Dashboard")
@ApiBaseSiteIdAndUserIdParam
@RequestMapping(value = "/{baseSiteId}/users/{userId}/dashboard")
public class CxDashboardController extends BaseController {

	@Resource(name = "cxDashboardFacade")
	private CxDashboardFacade dashboardFacade;

	@GetMapping
	@Operation(operationId = "getDashboardConfigs")
	public List<CxDashboardConfigWsDTO> getDashboardConfigs(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		List<CxDashboardConfigData> configs = dashboardFacade.getDashboardConfigsForCurrentUser();
		return getDataMapper().mapAsList(configs, CxDashboardConfigWsDTO.class, fields);
	}

	// TODO add a specific get method for the configuration by {code} with path segment and operationId
	// "getDashboardConfigByCode"

	@PutMapping
	@Operation(operationId = "updateDashboardConfig")
	/** TODO add "{code}" as path segment for this method */
	public CxDashboardConfigWsDTO updateDashboardConfig(@Parameter(description = "the updated dashboard config") @RequestBody CxDashboardConfigWsDTO config,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		CxDashboardConfigData data = getDataMapper().map(config, CxDashboardConfigData.class, fields);
		return getDataMapper().map(dashboardFacade.saveDashboardConfigForCurrentUser(data), CxDashboardConfigWsDTO.class, fields);
	}

	@DeleteMapping
	@Operation(operationId = "deleteDashboardConfig")
	/** TODO add "{code}" as path segment for this method */
	public void deleteDashboardConfig(@Parameter(description = "the dashboard config to be deleted") @RequestBody CxDashboardConfigWsDTO config) {
		dashboardFacade.deleteDashboardConfigForCurrentUser(config.getCode());
	}

	@GetMapping("/widgets")
	@Operation(operationId = "getAvailableDashboardWidgets")
	public List<CxDashboardWidgetWsDTO> getAvailableDashboardWidgets(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
		List<CxDashboardWidgetData> widgets = dashboardFacade.getAllAvailableDashboardWidgets();
		return getDataMapper().mapAsList(widgets, CxDashboardWidgetWsDTO.class, fields);
	}
}
