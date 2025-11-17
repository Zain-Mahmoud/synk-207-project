/**
 * Gateways (interfaces) between the use-case layer and external systems.
 * <p>
 * - TaskGateway, UserGateway: abstract interface persistence for core entities.
 * - CalendarGateway: abstract interface access to Google Calendar.
 * <p>
 * Use cases depend only on these interfaces.
 * Implementations live in the data_access layer.
 */
package use_case.gateways;