workspace "Veyra Platform - System Landscape" "High-level map of the Veyra ecosystem within the nursing home enterprise." {

  model {

    !impliedRelationships false

    // --- People OUTSIDE the enterprise boundary ---
    visitor       = person "Visitor"                    "Anonymous user who explores the platform and initiates registration."              "Visitor"
    relative      = person "Relative"                   "Family member who monitors resident health status and location remotely."          "Relative"
    doctor        = person "Doctor"                     "Monitors residents health data and reviews clinical reports."                     "Doctor"
    healthcareStaff         = person "Healthcare Staff "                      "Registers and updates resident clinical data at the facility."                    "Nurse"
    administrator = person "Nursing Home Administrator" "Manages facility operations, staff, residents, and subscriptions."                "Admin"

    // --- Enterprise boundary ---
    group "Veyra Nursing Home" {

      // Internal people
      support             = person "Support"                       "Handles internal technical support and operational assistance."          "Support"
      subscriptionManager = person "Subscription Services Manager" "Manages subscription plans, billing cycles, and client accounts."       "SubscriptionManager"

      // Internal software system
          vitalSignsSensors = softwareSystem "Vital Signs Sensor Hardware" "IoT devices that capture residents vital signs in real time."         "External, Hardware"
          gpsTrackers       = softwareSystem "GPS Tracker Hardware"        "Physical GPS devices for real-time resident location tracking."       "External, Hardware"
      veyraPlatform = softwareSystem "Veyra Platform" "IoT-based monolithic platform for nursing home management. Covers resident monitoring, vital signs, GPS tracking, alerts, and care records." "Internal"

    }

    // --- External software systems ---
    stripe            = softwareSystem "Stripe"                      "Processes monthly and annual subscription payments."                  "External"
    cloudinary        = softwareSystem "Cloudinary"                  "Cloud storage and management of resident media assets."               "External"
    googleMaps        = softwareSystem "Google Maps"                 "Provides geolocation resolution and real-time mapping services."      "External"
    twilio            = softwareSystem "Twilio"                      "Dispatches SMS and email alert notifications to users."               "External"


    // --- Relationships: People → Veyra Platform ---
    visitor             -> veyraPlatform "Explores platform features and initiates registration"               "HTTPS"
    doctor              -> veyraPlatform "Reviews resident health data and clinical reports"                   "HTTPS"
   healthcareStaff                  -> veyraPlatform "Records and updates resident clinical and operational data"          "HTTPS"
    administrator       -> veyraPlatform "Administers residents, staff, facility operations and subscription"  "HTTPS"
    relative            -> veyraPlatform "Monitors resident health status and real-time location"              "HTTPS"
    support             -> veyraPlatform "Provides technical support and manages operational issues"           "HTTPS"
    subscriptionManager -> veyraPlatform "Manages subscription plans and billing for client accounts"         "HTTPS"

    // --- Relationships: Veyra Platform → External Systems ---
    veyraPlatform -> stripe            "Processes subscription payment transactions"                "REST API / HTTPS"
    veyraPlatform -> cloudinary        "Stores and retrieves resident media assets"                 "REST API / HTTPS"
    veyraPlatform -> googleMaps        "Resolves coordinates and retrieves mapping data"            "REST API / HTTPS"
    veyraPlatform -> twilio            "Sends SMS and email alert notifications"                   "REST API / HTTPS"
    veyraPlatform -> vitalSignsSensors "Collects real-time vital signs telemetry from IoT devices" "Edge / TCP"
    veyraPlatform -> gpsTrackers       "Receives real-time GPS location telemetry"                 "Edge / TCP"

  }

  views {

    systemLandscape "SystemLandscape" "Veyra Nursing Home — System Landscape" {
      include *
      autoLayout
    }

    styles {
      element "Person" {
        shape      Person
        background "#ffffff"
        color      "#0f766e"
        stroke     "#0f766e"
        fontSize   20
      }
      element "Internal" {
        shape      RoundedBox
        background "#f0fdf4"
        color      "#065f46"
        stroke     "#065f46"
        fontSize   20
      }
      element "External" {
        shape      RoundedBox
        background "#6b7280"
        color      "#ffffff"
        stroke     "#6b7280"
        fontSize   20
      }
      element "Hardware" {
        shape      RoundedBox
        background "#4b5563"
        color      "#ffffff"
        stroke     "#4b5563"
        fontSize   20
      }
      element "Visitor" {
        background "#9ca3af"
        stroke     "#9ca3af"
        color      "#ffffff"
      }
      element "Doctor" {
        background "#2563eb"
        stroke     "#2563eb"
        color      "#ffffff"
      }
      element "Nurse" {
        background "#10b981"
        stroke     "#10b981"
        color      "#ffffff"
      }
      element "Admin" {
        background "#7c3aed"
        stroke     "#7c3aed"
        color      "#ffffff"
      }
      element "Relative" {
        background "#f59e0b"
        stroke     "#f59e0b"
        color      "#ffffff"
      }
      element "Support" {
        background "#0ea5e9"
        stroke     "#0ea5e9"
        color      "#ffffff"
      }
      element "SubscriptionManager" {
        background "#e11d48"
        stroke     "#e11d48"
        color      "#ffffff"
      }
    }

  }

}