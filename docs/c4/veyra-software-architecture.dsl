workspace "Veyra Platform" "IoT-based healthcare monitoring platform for nursing homes." {
  model {
    visitor               = person "Visitor"                       "Anonymous user who browses the landing page and initiates the registration flow."                                       "Visitor"
    doctor                = person "Doctor"                        "Monitors resident vital signs, reviews clinical reports, configures alert thresholds, and validates care plans."        "Doctor"
    healthcareStaff       = person "Healthcare Staff"              "Registers residents, records clinical observations, logs activity participation, and updates care plans."               "healthcareStaff"
    nursingHomeAdministration = person "Nursing Home Administrator" "Manages facility operations, staff scheduling, resident admissions, billing, and operational analytics."              "Admin"
    relative              = person "Relative"                      "Tracks assigned resident health status and GPS location; acknowledges safety alerts."                                  "Relative"

    stripe             = softwareSystem "Stripe"                      "Processes subscription payments, manages billing cycles, and dispatches payment lifecycle webhooks."                "External"
    cloudinary         = softwareSystem "Cloudinary"                  "Stores and delivers user and resident avatars via CDN with on-the-fly transformation support."                     "External"
    googleMaps         = softwareSystem "Google Maps"                 "Provides reverse geocoding and map tile rendering for geofence boundary visualization."                            "External"
    vitalSignsHardware = softwareSystem "Vital Signs Sensor Hardware" "IoT wearable that continuously measures heart rate, SpO2, temperature, and blood pressure."                       "External, Hardware"
    gpsHardware        = softwareSystem "GPS Tracker Hardware"        "GPS wearable that acquires real-time location fixes and transmits raw telemetry."                                  "External, Hardware"
    twilio             = softwareSystem "Twilio"                      "Delivers outbound SMS and email notifications to doctors, staff, administrators, and relatives."                   "External"

    veyraPlatform = softwareSystem "Veyra Platform" "IoT-based healthcare monitoring platform for nursing homes." {
      nginx    = container "Proxy nginx"      "Reverse proxy that routes external requests to the backend application."
      mysqlDB  = container "MySQL Database"   "Relational store for structured domain data: users, residents, care records, staff, roles, subscriptions, and payments."  "MySQL 8"   "Database"
      mongoDB  = container "MongoDB Database" "Document and time-series store for high-frequency IoT data: vital signs, GPS history, geofence events, and audit logs."   "MongoDB 7" "Database"

      embeddedApp = container "Vital Signs Embedded Application" "Wearable firmware that acquires biometric sensor readings and streams telemetry to the Edge Application." "C++" "IoT" {
        deviceController = component "Device Controller" "Initializes onboard sensors, manages acquisition cycles (heart rate, SpO2, temperature, blood pressure), and forwards structured telemetry to the Edge Application." "C++"
      }

      embeddedAppB = container "Tracker GPS Embedded Application" "GPS tracker firmware that acquires GNSS location fixes and transmits telemetry to the Edge Application." "C++" "IoT" {
        gpsController = component "GPS Controller" "Interfaces with the GNSS module, parses NMEA sentences into coordinate payloads, and streams location telemetry to the Edge Application. Handles signal loss and fix recovery." "C++"
      }

      edgeApp = container "Veyra Edge Application" "Edge gateway service deployed on the nursing home local network. Aggregates, filters, and batches IoT telemetry before forwarding to the backend API." "Python" "Edge"

      landingPage    = container "Landing Page"    "Public static site presenting features, pricing, and registration entry points. Redirects mobile visitors to app stores." "HTML, CSS, JavaScript" "WebBrowser"
      webApplication = container "Web Application" "Nginx static file server that delivers the compiled Angular SPA bundle to the browser." "Nginx" "WebApplication"

      monolithBackend = container "Veyra API Application" "Monolithic Spring Boot backend exposing a versioned REST API. Implements all business logic, IoT ingestion, RBAC enforcement, and third-party integrations, organized as DDD Bounded Contexts." "Java, Spring Boot" "Monolith" {

        sharedBC = component "Shared Bounded Context" \
          "Cross-cutting kernel with no business logic. Provides common value objects (ResidentId, NursingHomeId, StaffId, UserId), domain event base classes, event bus interface, exception hierarchy, audit logging, and pagination utilities. All BCs depend on Shared; Shared depends on nothing." \
          "Spring Boot Package"

        iamBC = component "IAM Bounded Context" \
          "Manages the full identity lifecycle: registration, bcrypt credential storage, JWT issuance, refresh token rotation, RBAC role assignment (Doctor, Nurse, Admin, Relative), and session invalidation. Publishes UserRegisteredEvent and PasswordResetRequestedEvent." \
          "Spring Boot Package"

        profileBC = component "Profile Bounded Context" \
          "Single source of truth for personal identity: name, DOB, contact details, and avatar (Cloudinary CDN reference) for all platform users, plus nursing home organizational profiles. Consumed by Nursing BC and HCM BC." \
          "Spring Boot Package"

        healthBC = component "Health Bounded Context" \
          "Ingests vital signs telemetry from the Edge layer. Persists time-series readings to MongoDB and threshold configurations to MySQL. Evaluates per-resident thresholds and publishes HealthAlertEvent, consumed by Communication BC." \
          "Spring Boot Package"

        nursingBC = component "Nursing Bounded Context" \
          "Core care domain. Owns CarePlan (medication schedules, nursing notes, care objectives) and ResidentAdmission (admission, discharge, room assignment) aggregates." \
          "Spring Boot Package"

        trackingBC = component "Tracking Bounded Context" \
          "Consumes GPS telemetry from the Edge layer. Persists location history and geofence definitions to MongoDB. Evaluates per-resident geofence rules and publishes GeofenceBreachEvent. Exposes current location to Health BC. Resolves coordinates via Google Maps." \
          "Spring Boot Package"

        activitiesBC = component "Activities Bounded Context" \
          "Owns the Activity Catalog and resident Activity Schedules. Records staff-submitted participation entries (attendance, engagement, notes). Exposes participation statistics to Nursing BC." \
          "Spring Boot Package"

        hcmBC = component "HCM Bounded Context" \
          "Owns Staff (contract, role, license, unit) and WorkShift (type, date range, coverage) aggregates. Manages nursing home organizational structure. Provides staff availability to Nursing BC." \
          "Spring Boot Package"

        analyticsBC = component "Analytics Bounded Context" \
          "Read-model BC. Produces operational KPIs from Nursing BC (admissions, care workload) and HCM BC (shift records, workforce ratios). Outputs: bed occupancy, length of stay, staff-to-resident ratios, and shift efficiency. Owns no aggregates and performs no domain writes." \
          "Spring Boot Package"

        communicationBC = component "Communication Bounded Context" \
          "Notification orchestrator. Subscribes to domain events from IAM, Health, Tracking, and Subscription BCs. Resolves recipients and channels, dispatches SMS and email via Twilio, and persists delivery records." \
          "Spring Boot Package"

        subscriptionBC = component "Subscription and Payments Bounded Context" \
          "Owns SubscriptionPlan (tier, features, seats, billing interval) and PaymentTransaction (amount, status, Stripe reference) aggregates. Integrates with Stripe for payment lifecycle. Publishes SubscriptionActivatedEvent, PaymentFailedEvent, and PlanRenewalEvent." \
          "Spring Boot Package"

        iamBC           -> sharedBC "Consumes value objects, domain event base classes, audit logging, and exception hierarchy"
        profileBC       -> sharedBC "Consumes UserId, ResidentId value objects and audit logging"
        healthBC        -> sharedBC "Consumes ResidentId, HealthAlertEvent base class, and audit logging"
        nursingBC       -> sharedBC "Consumes ResidentId, StaffId, domain event base classes, and audit logging"
        trackingBC      -> sharedBC "Consumes ResidentId, GeofenceBreachEvent base class, and audit logging"
        activitiesBC    -> sharedBC "Consumes ResidentId, Timestamp value objects and audit logging"
        hcmBC           -> sharedBC "Consumes StaffId, NursingHomeId value objects and audit logging"
        analyticsBC     -> sharedBC "Consumes pagination wrappers and data-export interfaces"
        communicationBC -> sharedBC "Consumes domain event base classes, event bus interfaces, and audit logging"
        subscriptionBC  -> sharedBC "Consumes NursingHomeId, domain event base classes, and audit logging"

        nursingBC -> profileBC "Reads resident personal data (name, DOB, contact) to enrich care plan context" "Internal API"
        hcmBC     -> profileBC "Reads staff personal data (name, contact, avatar)"                             "Internal API"

        nursingBC      -> iamBC "Validates JWT and enforces Doctor/Admin RBAC for care plan and admission operations" "Internal API"
        hcmBC          -> iamBC "Validates JWT and enforces Admin RBAC for staff and shift management"               "Internal API"
        subscriptionBC -> iamBC "Validates admin identity before exposing billing and plan operations"                "Internal API"

        iamBC -> communicationBC "Publishes UserRegisteredEvent and PasswordResetRequestedEvent" "Domain Event"

        subscriptionBC -> communicationBC "Publishes SubscriptionActivatedEvent, PaymentFailedEvent, and PlanRenewalEvent" "Domain Event"

        healthBC -> trackingBC "Queries current resident GPS location to correlate position with a vital-signs threshold breach before publishing HealthAlertEvent" "Internal API"

        nursingBC   -> healthBC  "Reads current vital signs and active alert history to inform care plan decisions"               "Internal API"
        nursingBC   -> hcmBC     "Queries staff by role, license, and shift availability for care plan assignment"                "Internal API"
        analyticsBC -> nursingBC "Reads admission, discharge, and care workload records for bed occupancy and turnover metrics"   "Internal API"
        analyticsBC -> hcmBC     "Reads shift records and workforce composition for staff-to-resident ratio analytics"            "Internal API"

        communicationBC -> healthBC   "Subscribes to HealthAlertEvent — dispatches SMS and email to assigned doctor and staff"         "Domain Event"
        communicationBC -> trackingBC "Subscribes to GeofenceBreachEvent — dispatches SMS and push notification to relatives and staff" "Domain Event"

        iamBC           -> mysqlDB "Persists user accounts, hashed credentials, RBAC roles, refresh tokens, and session records"  "JDBC/JPA"
        profileBC       -> mysqlDB "Persists user profiles, avatar CDN references, and nursing home organizational records"       "JDBC/JPA"
        nursingBC       -> mysqlDB "Persists care plans, medication schedules, nursing notes, and admission/discharge records"    "JDBC/JPA"
        hcmBC           -> mysqlDB "Persists staff records, contracts, role assignments, unit structure, and shift schedules"     "JDBC/JPA"
        activitiesBC    -> mysqlDB "Persists activity catalog entries, resident schedules, and participation records"             "JDBC/JPA"
        subscriptionBC  -> mysqlDB "Persists subscription plans, billing cycles, payment transactions, and Stripe webhook log"   "JDBC/JPA"
        analyticsBC     -> mysqlDB "Reads Nursing and HCM data for KPI aggregation and report generation"                        "JDBC/JPA"
        healthBC        -> mysqlDB "Persists per-resident threshold configurations and health alert metadata"                     "JDBC/JPA"
        healthBC        -> mongoDB "Persists vital signs time-series (heart rate, SpO2, temperature, blood pressure)"            "MongoDB Driver"
        trackingBC      -> mongoDB "Persists GPS location history, geofence definitions, and breach event records"               "MongoDB Driver"

        communicationBC -> twilio     "Dispatches SMS and email for health alerts, geofence breaches, onboarding, and billing events" "REST API"
        subscriptionBC  -> stripe     "Creates payment intents, confirms transactions, and processes billing webhooks"                "REST API"
        profileBC       -> cloudinary "Uploads and retrieves avatar images via CDN"                                                   "REST API"
        trackingBC      -> googleMaps "Resolves GPS coordinates to addresses and retrieves map tiles for geofence visualization"       "REST API"

        edgeApp -> trackingBC "Forwards filtered GPS telemetry for geofence evaluation, location persistence, and breach detection" "REST/HTTPS"
      }

      webClientApp = container "Veyra Web Client Application" "Angular SPA for doctors, healthcare staff, and administrators. Communicates with the Veyra API over REST/HTTPS." "TypeScript, Angular" "Browser" {

        wSharedBC        = component "Shared Bounded Context"        "Client kernel. JWT interceptors, common value objects, base error handlers, pagination models, and shared UI utilities." "Angular"
        wAuthBC          = component "IAM Bounded Context"           "Login and registration flows. In-memory JWT storage, silent token refresh, RBAC route guards, and session termination." "Angular"
        wProfileBC       = component "Profile Bounded Context"       "View and edit user profiles and nursing home organizational data. Handles avatar selection before upload."              "Angular"
        wHealthBC        = component "Health Bounded Context"        "Real-time vital signs dashboards, threshold configuration, alert history, and trend analysis."                          "Angular"
        wNursingBC       = component "Nursing Bounded Context"       "Care plan management, medication schedules, nursing notes, and admission/discharge workflows."                          "Angular"
        wActivitiesBC    = component "Activities Bounded Context"    "Activity catalog, resident schedule management, and staff participation recording."                                     "Angular"
        wHcmBC           = component "HCM Bounded Context"           "Staff directory, credential forms, role and unit assignment, and shift scheduling."                                     "Angular"
        wAnalyticsBC     = component "Analytics Bounded Context"     "Operational KPI dashboards with date filtering and CSV/PDF export."                                                     "Angular"
        wCommunicationBC = component "Communication Bounded Context" "Notification center showing alert history by type, delivery status, and channel. Allows staff to acknowledge alerts."  "Angular"
        wSubscriptionBC  = component "Subscription Bounded Context"  "Plan display, billing history, Stripe payment method management, and plan upgrade/cancellation flows."                 "Angular"

        wAuthBC          -> wSharedBC "Consumes JWT interceptors, route guard base classes, and base error handlers"
        wProfileBC       -> wSharedBC "Consumes UserId, NursingHomeId value objects and loading-state utilities"
        wHealthBC        -> wSharedBC "Consumes ResidentId value object and pagination models for alert history"
        wNursingBC       -> wSharedBC "Consumes ResidentId, StaffId value objects and form validation utilities"
        wActivitiesBC    -> wSharedBC "Consumes ResidentId value object and shared calendar utilities"
        wHcmBC           -> wSharedBC "Consumes StaffId, NursingHomeId value objects and pagination wrappers"
        wAnalyticsBC     -> wSharedBC "Consumes pagination wrappers, date range models, and export interfaces"
        wCommunicationBC -> wSharedBC "Consumes notification event models and acknowledgment status types"
        wSubscriptionBC  -> wSharedBC "Consumes NursingHomeId value object and shared API error handlers"
      }

      mobileApp = container "Veyra Mobile Application" "Flutter app for iOS and Android serving relatives and healthcare staff. Monitors residents on the go and receives real-time push notifications." "Flutter, Dart" "MobileApp" {

        mSharedBC        = component "Shared Bounded Context"        "Mobile kernel. Dio JWT interceptors, common value objects, offline state detection, base error handlers, and shared UI utilities."               "Flutter"
        mAuthBC          = component "IAM Bounded Context"           "Mobile login, hardware-backed JWT storage (Keychain/Keystore), refresh token rotation, biometric authentication, and session termination."       "Flutter"
        mProfileBC       = component "Profile Bounded Context"       "View and edit user profile and nursing home contact data. Supports camera and gallery avatar selection."                                         "Flutter"
        mHealthBC        = component "Health Bounded Context"        "Real-time vital signs display with threshold indicators, active alert feed, and resident health summary cards."                                  "Flutter"
        mNursingBC       = component "Nursing Bounded Context"       "Active care plan viewer, daily medication schedule with administered status, and inline nursing note creation."                                  "Flutter"
        mActivitiesBC    = component "Activities Bounded Context"    "Resident activity schedule viewer and mobile participation registration with attendance, engagement level, and session notes."                   "Flutter"
        mCommunicationBC = component "Communication Bounded Context" "Receives push notifications for health breaches and geofence violations. In-app alert feed with acknowledgment support for staff and relatives." "Flutter"

        mAuthBC          -> mSharedBC "Consumes JWT interceptors, secure storage interfaces, biometric auth utilities, and base error handlers"
        mProfileBC       -> mSharedBC "Consumes UserId, NursingHomeId value objects and image picker utilities"
        mHealthBC        -> mSharedBC "Consumes ResidentId value object and offline state utilities for cached data display"
        mNursingBC       -> mSharedBC "Consumes ResidentId, StaffId value objects and offline state utilities"
        mActivitiesBC    -> mSharedBC "Consumes ResidentId value object and connectivity monitoring for offline participation queuing"
        mCommunicationBC -> mSharedBC "Consumes notification event models, severity types, and push payload parsing utilities"
      }

      embeddedAppB   -> gpsHardware       "Acquires raw GNSS location telemetry from onboard hardware module"
      embeddedApp    -> vitalSignsHardware "Acquires raw biometric sensor readings from onboard wearable sensors"
      landingPage    -> webApplication    "Delegates Angular SPA bundle delivery to Nginx"              "HTTP"
      webApplication -> webClientApp      "Delivers compiled Angular SPA bundle to the browser"         "HTTP"
      webClientApp   -> nginx             "Invokes REST API endpoints for all domain operations"        "REST/HTTPS"
      mobileApp      -> nginx             "Invokes REST API endpoints for all domain operations"        "REST/HTTPS"
      edgeApp        -> embeddedApp       "Pulls vital signs telemetry stream for edge aggregation"
      edgeApp        -> embeddedAppB      "Pulls GPS telemetry stream for edge aggregation"
      landingPage    -> mobileApp         "Redirects mobile visitors to the iOS App Store or Google Play" "HTTPS"
      webClientApp   -> iamBC             "Submits credentials and refresh tokens to obtain JWT session tokens" "REST/HTTPS"
      mobileApp      -> iamBC             "Submits credentials and refresh tokens to obtain JWT session tokens" "REST/HTTPS"
      nginx          -> monolithBackend   "Proxies API requests to the monolithic backend application" "HTTP"

      wAuthBC          -> monolithBackend "POST /auth/login, /auth/refresh — JWT token acquisition and renewal"                              "REST/HTTPS"
      wProfileBC       -> monolithBackend "GET/PUT /profiles — reads and updates user and nursing home profile data"                         "REST/HTTPS"
      wHealthBC        -> monolithBackend "GET /health/vitals, /alerts, /thresholds — vital signs, alert history, and threshold config"      "REST/HTTPS"
      wNursingBC       -> monolithBackend "CRUD /care-plans, /medications, /notes, /admissions — full care plan lifecycle"                   "REST/HTTPS"
      wActivitiesBC    -> monolithBackend "GET /activities, POST /participation — catalog reads and participation submission"                 "REST/HTTPS"
      wHcmBC           -> monolithBackend "CRUD /staff, /shifts — staff records and shift scheduling"                                        "REST/HTTPS"
      wAnalyticsBC     -> monolithBackend "GET /analytics — KPI metrics and operational reports with date and filter params"                 "REST/HTTPS"
      wCommunicationBC -> monolithBackend "GET /notifications, POST /acknowledgments — alert history and acknowledgment submission"           "REST/HTTPS"
      wSubscriptionBC  -> monolithBackend "GET/PUT /subscriptions, /payments — plan details and billing operations"                          "REST/HTTPS"

      mAuthBC          -> monolithBackend "POST /auth/login, /auth/refresh — mobile session initialization and renewal"                      "REST/HTTPS"
      mProfileBC       -> monolithBackend "GET/PUT /profiles/me — reads and updates authenticated user profile from mobile"                  "REST/HTTPS"
      mHealthBC        -> monolithBackend "GET /health/vitals, /alerts — real-time vital signs and alert history for assigned residents"     "REST/HTTPS"
      mNursingBC       -> monolithBackend "GET /care-plans, /medications, /notes; POST /notes — care data for assigned residents"            "REST/HTTPS"
      mActivitiesBC    -> monolithBackend "GET /activities/schedule, POST /participation — schedule reads and participation submission"       "REST/HTTPS"
      mCommunicationBC -> monolithBackend "GET /notifications, POST /acknowledgments — push alerts and acknowledgment submission"            "REST/HTTPS"
    }

    visitor               -> landingPage              "Explores features and initiates registration"                                    "HTTPS"
    visitor               -> nursingHomeAdministration "Registers as a nursing home administrator"
    visitor               -> relative                 "Registers as a resident relative after administrator invitation"
    doctor                -> webApplication             "Monitors vital signs, reviews clinical reports, and manages alert thresholds"    "HTTPS"
    nursingHomeAdministration -> webApplication         "Manages admissions, staff scheduling, analytics, and subscription billing"      "HTTPS"
    nursingHomeAdministration -> mobileApp            "Monitors residents, staff activity, and operational analytics on the go"
    relative              -> mobileApp                "Tracks assigned resident health status and location in real time"                "HTTPS"
    healthcareStaff       -> mobileApp                "Monitors residents and registers care data during rounds"                       "HTTPS"
  }

  views {
    systemContext veyraPlatform "SystemContext" {
      include *
      autoLayout
    }
    container veyraPlatform "Containers" {
      include *
      autoLayout
    }
    component webClientApp "WebClientComponents" {
      include *
      autoLayout
    }
    component mobileApp "MobileAppComponents" {
      include *
      autoLayout
    }
    component monolithBackend "MonolithComponents" {
      include *
      include webClientApp
      include mobileApp
      autoLayout
    }

    styles {
      element "Person" {
        shape Person
        background "#ffffff"
        color "#0f766e"
        stroke "#0f766e"
        fontSize 20
      }
      element "Software System" {
        background "#ffffff"
        color "#0f766e"
        stroke "#0f766e"
        shape RoundedBox
        fontSize 20
      }
      element "External" {
        background "#6b7280"
        color "#ffffff"
        stroke "#6b7280"
      }
      element "Hardware" {
        background "#4b5563"
        color "#ffffff"
        stroke "#4b5563"
      }
      element "Visitor" {
        background "#9ca3af"
        stroke "#9ca3af"
        color "#ffffff"
      }
      element "Doctor" {
        background "#2563eb"
        stroke "#2563eb"
        color "#ffffff"
      }
      element "healthcareStaff" {
        background "#10b981"
        stroke "#10b981"
        color "#ffffff"
      }
      element "WebApplication" {
        shape RoundedBox
        background "#f8fafc"
        color "#334155"
        stroke "#334155"
      }
      element "Admin" {
        background "#7c3aed"
        stroke "#7c3aed"
        color "#ffffff"
      }
      element "Relative" {
        background "#f59e0b"
        stroke "#f59e0b"
        color "#ffffff"
      }
      element "Monolith" {
        shape RoundedBox
        background "#f0fdf4"
        color "#065f46"
        stroke "#065f46"
      }
      element "MobileApp" {
        shape MobileDeviceLandscape
        background "#fefce8"
        color "#92400e"
        stroke "#92400e"
      }
      element "Browser" {
        shape WebBrowser
        background "#f0fdf4"
        color "#0f766e"
        stroke "#0f766e"
      }
      element "WebBrowser" {
        shape WebBrowser
        background "#ffffff"
        color "#0f766e"
        stroke "#0f766e"
      }
      element "Database" {
        shape Cylinder
        background "#e0f2fe"
        color "#0369a1"
        stroke "#0369a1"
      }
    }
  }
}