workspace "Veyra Platform" "A software platform..." {
  model {
    visitor = person "Visitor" "An anonymous who navigates across the landing page." "Visitor"
    doctor = person "Doctor" "Uses the platform to monitor residents." "Doctor"
    nurse = person "Nurse" "Registers and updates resident data." "Nurse"
    nursingHomeAdministration = person "Nursing Home Administrator" "Manages operations and residents." "Admin"
    relative = person "Relative" "Checks resident status and alerts." "Relative"

    stripe = softwareSystem "Stripe" "Processes monthly/annual subscription." "External"
    cloudinary = softwareSystem "Cloudinary" "Cloud service for image storage and management." "External"
    googleMaps = softwareSystem "Google Maps" "Provides real-time location and mapping services." "External"
    vitalSignsHardware = softwareSystem "Vital Signs Sensor Hardware" "IoT devices that collect residents vital signs." "External, Hardware"
    gpsHardware = softwareSystem "GPS Tracker Hardware" "Physical GPS device for real-time location tracking." "External, Hardware"
    twilio = softwareSystem "Twilio" "Handles email notifications." "External"

    veyraPlatform = softwareSystem "Veyra Platform" "IoT-based healthcare monitoring platform." {

      embeddedApp = container "Vital Signs Embedded Application" "Embedded software controlling the Vital Signs hardware." "C++" "IoT" {
        deviceController = component "Device Controller" "Interfaces with sensors, and display." "C++"
      }

      embeddedAppB = container "Tracker GPS Embedded Application" "Embedded software controlling the Tracker GPS hardware." "C++" "IoT" {
        gpsController = component "GPS Controller" "Interfaces with GPS hardware and transmits location data." "C++"
      }

      landingPage = container "Landing Page" "Serves the public-facing static website. Provides platform information and sign-up access." "HTML, CSS, JavaScript" "WebBrowser"
      webApplication = container "Web Application" "Delivers the compiled Angular SPA bundle to the users browser. Acts as a static file server." "Nginx" "WebApplication"

      webClientApp = container "Veyra Web Client Application" "Single Page Application running in the users browser. Used by doctors, nurses, admins, and relatives." "TypeScript, Angular" "Browser" {
      }

      mobileApp = container "Veyra Mobile Application" "Cross-platform mobile app for relatives and nursing staff to monitor residents on the go." "Flutter, Dart" "MobileApp" {
      }

      monolithBackend = container "Veyra API Application" "Monolithic backend that exposes a REST API and handles all business logic, domain rules, and third-party integrations." "Java, Spring Boot" "Monolith" {
      }

      edgeApp = container "Veyra Edge Application" "Edge software for data processing of vital signs and GPS telemetry." "Python" "Edge" {
      }

      mysqlDB = container "MySQL Database" "Stores all relational and transactional data: users, residents, care records, roles, and subscriptions." "MySQL 8" "Database"
      mongoDB = container "MongoDB Database" "Stores IoT time-series data: vital signs readings, GPS history, threshold events, and system logs." "MongoDB 7" "Database"

      monolithBackend -> mysqlDB "Persists and queries structured domain data" "JDBC/JPA"
      monolithBackend -> mongoDB "Persists and queries IoT telemetry and event streams" "MongoDB Driver"
      monolithBackend -> twilio "Dispatches SMS and email alert notifications" "REST API"
      monolithBackend -> stripe "Initiates and confirms subscription payment transactions" "REST API"
      monolithBackend -> cloudinary "Manages resident media assets" "REST API"
      monolithBackend -> googleMaps "Resolves geolocation coordinates and retrieves mapping data" "REST API"
      embeddedAppB -> gpsHardware "Acquires raw location telemetry"
      embeddedApp -> vitalSignsHardware "Acquires raw vital signs telemetry"
      landingPage -> webApplication "Delegates SPA bundle delivery" "HTTP"
      webApplication -> webClientApp "Delivers compiled SPA bundle to browser" "HTTP"
      webClientApp -> monolithBackend "Invokes REST API endpoints"
      mobileApp -> monolithBackend "Invokes REST API endpoints"
      edgeApp -> embeddedApp "Acquires vital signs data stream for edge processing"
      edgeApp -> embeddedAppB "Acquires GPS telemetry stream for edge processing"
      edgeApp -> monolithBackend "Forwards aggregated vital signs data, threshold alerts, GPS data and geofence events"
     landingPage -> mobileApp "Redirects users to download the mobile application" "HTTPS"
    }

    visitor -> landingPage "Explores platform features and initiates registration" "HTTPS"
    visitor -> nursingHomeAdministration "Registers as a nursing home administrator"
    visitor -> relative "Registers as a resident's relative"
    doctor -> webClientApp "Oversees resident health data and clinical reports" "HTTPS"
    nurse -> webClientApp "Records and updates resident clinical data" "HTTPS"
    nursingHomeAdministration -> webClientApp "Administers resident operations and facility management" "HTTPS"
    nursingHomeAdministration -> mobileApp "Oversees residents, staff activity, and operational analytics"
    relative -> mobileApp "Tracks resident health and location in real time" "HTTPS"
    nurse -> mobileApp "Monitors and updates resident data on the go" "HTTPS"

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
      element "Nurse" {
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