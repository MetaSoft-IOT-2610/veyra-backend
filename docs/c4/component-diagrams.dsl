workspace "Veyra Platform" "C4 model of the Veyra backend (Spring Boot modular monolith), organized by bounded context. Derived from the actual implementation as of develop branch." {

    model {
        admin       = person "Administrator" "Registers the nursing home, manages residents, staff, rooms and relatives."
        doctor      = person "Doctor" "Configures vital sign thresholds, diagnoses conditions, reviews clinical data."
        staff       = person "Healthcare Staff" "Nurses/caregivers who log activities and are assigned to residents."
        relative    = person "Relative" "Family member who tracks a resident and chats with staff."

        stripe      = softwareSystem "Stripe" "Payment gateway for subscriptions and payments." "External"
        cloudinary  = softwareSystem "Cloudinary" "Image hosting for profile photos and business logos." "External"
        sendgrid    = softwareSystem "SendGrid" "Transactional email delivery." "External"
        firebase    = softwareSystem "Firebase Cloud Messaging" "Push notification delivery to mobile/web clients." "External"

        # Physical wearable sensors are third-party hardware Veyra does not author firmware for (no verified
        # firmware/embedded source was reviewed) - modeled as external hardware. The gateway software that
        # authenticates against Veyra's own backend (EdgeGatewayAuthFilter) IS Veyra's software, deployed
        # on-premise, so it is modeled below as a container of Veyra Platform, not as an external system.
        vitalSignsHardware = softwareSystem "Vital Signs Sensor Hardware" "Third-party wearable that measures heart rate, temperature and oxygen saturation." "External, Hardware"
        gpsHardware        = softwareSystem "GPS Tracker Hardware" "Third-party wearable that acquires GPS location fixes." "External, Hardware"

        veyra = softwareSystem "Veyra Platform" "Real-time vital-sign monitoring platform for nursing homes." {

            webApp    = container "Web Application" "Dashboard used by Administrators and Doctors." "Angular" "Web"
            mobileApp = container "Mobile Application" "App used by Healthcare Staff and Relatives." "Mobile" "Mobile"

            edgeGateway = container "Edge Gateway" "On-premise gateway deployed on the nursing home's local network. Authenticates against the Backend API via custom X-Device-Id/X-Device-Mac headers (EdgeGatewayAuthFilter), is itself registered in Veyra's Device registry as an EDGE_GATEWAY-type device, forwards vital-sign/GPS telemetry batches, and can pull a device-registry delta to mirror it locally." "On-premise service" "Edge"

            api = container "Backend API" "Spring Boot modular monolith exposing REST + WebSocket/STOMP APIs. In a monolith, each module/bounded context is a component - it is NOT further decomposed into controller/service/domain/repository layers here (that breakdown only makes sense per-service in a microservices architecture)." "Spring Boot" {

                nursingComponent = component "Nursing" "Manages the nursing home, its rooms, resident admission/lifecycle, relatives (with self-service IAM account activation) and a simple medication registry. No CarePlan aggregate and no medication-administration workflow exist. REST: /api/v1/nursing-homes, /api/v1/residents, /api/v1/administrators, /api/v1/medications, /api/v1/relatives." "Spring Boot Module"

                trackingComponent = component "Tracking" "IoT device registry and vital-sign/GPS telemetry ingestion. Authenticates on-premise edge gateways via custom headers and broadcasts new readings in real time over WebSocket/STOMP. REST: /api/v1/devices, /api/v1/locations, /api/v1/measurements, /api/v1/edge/registry." "Spring Boot Module"

                healthComponent = component "Health" "Clinical anomaly-detection engine: evaluates incoming vital signs against doctor-configured thresholds and hard-coded safety limits, and manages allergy/medical-condition registries. REST: /api/v1/residents/{id}/allergies, /medical-conditions, /vital-sign-thresholds, /api/v1/resident/{id}/vital-signs." "Spring Boot Module"

                hcmComponent = component "HCM" "Manages nursing-home staff and their employment-contract history, including automatic IAM account provisioning for Doctor/Nurse roles. REST: /api/v1/nursing-homes/{id}/staff, /api/v1/staff." "Spring Boot Module"

                activitiesComponent = component "Activities" "Registers daily care activities (meal, bath, risk profile, recreational) assigned to a resident, with a PENDING/IN_PROGRESS/COMPLETED lifecycle. REST: /api/v1/activities, /api/v1/nursing-homes/{id}/activities." "Spring Boot Module"

                communicationComponent = component "Communication" "Messaging and notification hub: real-time chat over WebSocket/STOMP, in-app notifications, push notifications (Firebase) and transactional email (SendGrid). REST: /api/v1/conversations, /api/v1/email-notifications, /api/v1/push-notifications, /api/v1/users/{id}/notifications, /api/v1/users/{id}/push-tokens. Not called by the web or mobile clients today." "Spring Boot Module"

                iamComponent = component "IAM" "Identity and access management: JWT authentication, user/role management and an activation-token password-set flow used to onboard relatives and staff. REST: /api/v1/authentication, /api/v1/users, /ap/v1/roles." "Spring Boot Module"

                profilesComponent = component "Profiles" "Manages person and business profile data (identity beyond authentication), with photo/logo upload via Cloudinary. REST: /api/v1/person-profiles, /api/v1/business-profiles." "Spring Boot Module"

                paymentsComponent = component "Payments" "Manages subscriptions and payments, mirrored in real time to Stripe. REST: /api/v1/payments, /api/v1/subscriptions. Includes an unwired Stripe webhook handler (no HTTP endpoint exposes it)." "Spring Boot Module"

                analyticsComponent = component "Analytics" "Event-driven metrics projection: listens to domain events from Nursing (resident admitted/retired) and HCM (staff hired/terminated/suspended) and tallies counts per nursing home. Read-only REST: /api/v1/nursing-homes/{id}/residents-admissions, /staff-hires, /staff-terminations. No health/vitals or location data is aggregated (no Google Maps integration exists)." "Spring Boot Module"
            }

            relationalDb = container "Relational Database" "Stores NursingHome, Resident, Medication, Relative, Staff, User, Profile, Payment, Subscription, Metric and Device data." "PostgreSQL" "Database"
            documentDb   = container "Document Database" "Stores high-volume Location and Measurement telemetry." "MongoDB" "Database"
        }

        # people -> client containers
        admin -> webApp "Manages nursing home, residents, staff and rooms via"
        doctor -> webApp "Configures thresholds and reviews clinical data via"
        staff -> mobileApp "Logs activities and views assigned residents via"
        relative -> mobileApp "Tracks resident and chats via"

        webApp -> api "Makes API calls to" "HTTPS/JSON"
        mobileApp -> api "Makes API calls to" "HTTPS/JSON"
        webApp -> api "Subscribes to real-time updates from" "WebSocket/STOMP"
        mobileApp -> api "Subscribes to real-time updates from" "WebSocket/STOMP"

        vitalSignsHardware -> edgeGateway "Streams biometric sensor readings to (protocol not verified - no firmware source reviewed)"
        gpsHardware -> edgeGateway "Streams GPS fixes to (protocol not verified - no firmware source reviewed)"
        edgeGateway -> trackingComponent "Sends telemetry batches / pulls device registry delta" "HTTPS, header-based auth"

        # Persistence
        nursingComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        trackingComponent -> relationalDb "Device registry" "JPA/SQL"
        trackingComponent -> documentDb "Location / Measurement telemetry" "MongoDB driver"
        healthComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        hcmComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        activitiesComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        communicationComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        iamComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        profilesComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        paymentsComponent -> relationalDb "Reads from and writes to" "JPA/SQL"
        analyticsComponent -> relationalDb "Reads from and writes to" "JPA/SQL"

        # External systems
        communicationComponent -> sendgrid "Sends transactional email via" "HTTPS/REST"
        communicationComponent -> firebase "Sends push notifications via" "HTTPS/REST"
        profilesComponent -> cloudinary "Uploads/deletes photos via" "HTTPS/REST"
        paymentsComponent -> stripe "Creates customers / subscriptions / payment intents via" "HTTPS/REST"

        # Real-time push to clients
        trackingComponent -> webApp "Pushes live telemetry to" "WebSocket/STOMP"
        trackingComponent -> mobileApp "Pushes live telemetry to" "WebSocket/STOMP"
        communicationComponent -> webApp "Broadcasts messages to" "WebSocket/STOMP"
        communicationComponent -> mobileApp "Broadcasts messages to" "WebSocket/STOMP"

        # Cross-context synchronous calls (ACL facades, in-process direct calls between bounded contexts)
        nursingComponent -> iamComponent "createUser, fetchUserIdByUsername, createRelativeAccount" "Sync ACL"
        nursingComponent -> hcmComponent "validate nurse role / active contract" "Sync ACL"
        nursingComponent -> profilesComponent "create/update/delete Person & Business Profile" "Sync ACL"
        nursingComponent -> communicationComponent "sendRenderedTemplateEmail (relative activation)" "Sync ACL"

        healthComponent -> nursingComponent "resolve/validate resident, fetch assigned staff" "Sync ACL"
        healthComponent -> trackingComponent "resolve resident by device id" "Sync ACL"
        healthComponent -> hcmComponent "resolve user by staff id" "Sync ACL"
        healthComponent -> communicationComponent "sendPushNotificationToUser (anomaly alert)" "Sync ACL"

        hcmComponent -> nursingComponent "validate nursing home / resident-vs-staff DNI" "Sync ACL"
        hcmComponent -> profilesComponent "create/update Person Profile, fetch DNI" "Sync ACL"
        hcmComponent -> iamComponent "createUser (Doctor/Nurse)" "Sync ACL"
        hcmComponent -> communicationComponent "sendRenderedTemplateEmail (staff activation)" "Sync ACL"

        activitiesComponent -> nursingComponent "validate nursing home / resident exist" "Sync ACL"

        communicationComponent -> iamComponent "ensureUserExists" "Sync ACL"

        iamComponent -> nursingComponent "fetchAdministratorByUserId (sign-in)" "Sync ACL"
        iamComponent -> hcmComponent "getStaffByUserId (sign-in)" "Sync ACL"

        paymentsComponent -> iamComponent "existsUserById, fetchUserByUsername" "Sync ACL"

        analyticsComponent -> nursingComponent "validate nursing home exists / creation date" "Sync ACL"

        # Cross-context asynchronous communication (Spring domain events, in-process pub/sub)
        nursingComponent -> analyticsComponent "AdmittedResidentEvent / RetiredResidentEvent" "Async Domain Event"
        hcmComponent -> analyticsComponent "EmployeeHiredEvent / EmployeeTerminationEvent / EmployeeSuspendedEvent" "Async Domain Event"
        trackingComponent -> healthComponent "MeasurementRecordedEvent" "Async Domain Event"

        # Web / Mobile clients -> bounded contexts (derived from the real frontend/mobile source)
        webApp -> nursingComponent "CRUD nursing homes, residents, rooms, medications, relatives, administrators" "HTTPS/JSON"
        mobileApp -> nursingComponent "residents, rooms, relatives; administrators/staff nursing-home lookup" "HTTPS/JSON"

        webApp -> trackingComponent "CRUD devices, assign/unassign, change status" "HTTPS/JSON"
        mobileApp -> trackingComponent "devices/{id}/measurements (via the Family module)" "HTTPS/JSON"

        webApp -> healthComponent "allergies, vital-sign-thresholds, resident vital-signs" "HTTPS/JSON"
        mobileApp -> healthComponent "allergies, resident vital-signs" "HTTPS/JSON"

        webApp -> hcmComponent "staff CRUD, contracts" "HTTPS/JSON"
        mobileApp -> hcmComponent "staff CRUD, contracts" "HTTPS/JSON"

        webApp -> activitiesComponent "CRUD activities, complete" "HTTPS/JSON"
        mobileApp -> activitiesComponent "list/create/complete/delete activities" "HTTPS/JSON"

        webApp -> iamComponent "sign-in, sign-up" "HTTPS/JSON"
        mobileApp -> iamComponent "sign-in; GET users" "HTTPS/JSON"

        webApp -> profilesComponent "CRUD person/business profiles" "HTTPS/JSON"
        mobileApp -> profilesComponent "GET person-profiles (enrichment)" "HTTPS/JSON"

        webApp -> paymentsComponent "Intenta /api/payments/* - no coincide con /api/v1/payments ni /api/v1/subscriptions reales (integracion incompleta/rota)" "fetch(), sin interceptor de auth"
        mobileApp -> paymentsComponent "GET/POST users/{userId}/subscriptions - no coincide con /api/v1/subscriptions real (integracion probablemente rota)" "HTTPS/JSON"

        webApp -> analyticsComponent "residents-admissions, staff-hires, staff-terminations" "HTTPS/JSON"
        mobileApp -> analyticsComponent "residents-admissions, staff-hires, staff-terminations" "HTTPS/JSON"
    }

    views {

        systemLandscape "Landscape" "System landscape - Veyra Platform and its external systems." {
            include *
            autoLayout lr
        }

        systemContext veyra "Context" "System context - Veyra Platform." {
            include *
            autoLayout lr
        }

        container veyra "Containers" "Container diagram - Veyra Platform." {
            include *
            autoLayout lr
        }

        component api "BackendComponents" "Component diagram - overview of every bounded context inside the Backend API monolith. In a monolith, each bounded context IS a component." {
            include *
            autoLayout lr
        }

        component api "NursingComponents" "Component diagram - Nursing bounded context and its direct dependencies." {
            include nursingComponent iamComponent hcmComponent profilesComponent communicationComponent healthComponent activitiesComponent analyticsComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "TrackingComponents" "Component diagram - Tracking bounded context and its direct dependencies." {
            include trackingComponent healthComponent
            include relationalDb documentDb edgeGateway vitalSignsHardware gpsHardware webApp mobileApp
            autoLayout lr
        }

        component api "HealthComponents" "Component diagram - Health bounded context and its direct dependencies." {
            include healthComponent nursingComponent trackingComponent hcmComponent communicationComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "HcmComponents" "Component diagram - HCM bounded context and its direct dependencies." {
            include hcmComponent nursingComponent profilesComponent iamComponent communicationComponent healthComponent analyticsComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "ActivitiesComponents" "Component diagram - Activities bounded context and its direct dependencies." {
            include activitiesComponent nursingComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "CommunicationComponents" "Component diagram - Communication bounded context and its direct dependencies." {
            include communicationComponent iamComponent nursingComponent healthComponent hcmComponent
            include sendgrid firebase relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "IamComponents" "Component diagram - IAM bounded context and its direct dependencies." {
            include iamComponent nursingComponent hcmComponent paymentsComponent communicationComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "ProfilesComponents" "Component diagram - Profiles bounded context and its direct dependencies." {
            include profilesComponent nursingComponent hcmComponent
            include cloudinary relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "PaymentsComponents" "Component diagram - Payments (Subscriptions & Payments) bounded context and its direct dependencies." {
            include paymentsComponent iamComponent
            include stripe relationalDb webApp mobileApp
            autoLayout lr
        }

        component api "AnalyticsComponents" "Component diagram - Analytics bounded context and its direct dependencies." {
            include analyticsComponent nursingComponent hcmComponent
            include relationalDb webApp mobileApp
            autoLayout lr
        }

        styles {
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "External" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Web" {
                background #438dd5
                shape webBrowser
                color #ffffff
            }
            element "Mobile" {
                background #438dd5
                shape mobileDevicePortrait
                color #ffffff
            }
            element "Edge" {
                background #438dd5
                color #ffffff
            }
            element "Hardware" {
                background #666666
                color #ffffff
            }
            element "Database" {
                shape cylinder
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
        }
    }
}
