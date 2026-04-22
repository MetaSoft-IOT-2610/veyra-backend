workspace "Veyra Platform" "Production deployment architecture for the Veyra IoT patient-monitoring platform." {

    !identifiers hierarchical

    model {

        veyra = softwareSystem "Veyra Platform" "Provides real-time IoT-based patient monitoring and management for nursing homes." {

            landingPage = container "Landing Page" \
                "Static marketing website delivered to visitors." \
                "HTML / CSS / JavaScript"

            webApp = container "Web Application" \
                "Delivers the Veyra Angular SPA to the user's web browser." \
                "Static File Server"

            webClient = container "Veyra Web Client" \
                "Angular single-page application used by nursing staff and administrators to monitor patients." \
                "Angular SPA"

            mobileApp = container "Veyra Mobile App" \
                "Mobile application for nursing staff to monitor patients on the go." \
                "Flutter / Dart"

            api = container "Veyra API Application" \
                "Monolithic backend that provides all business logic and REST API services to clients." \
                "Monolithic Backend"

            mysqlDb = container "MySQL Database" \
                "Stores relational data: users, patients, rooms, configurations, and audit logs." \
                "MySQL" {
                tags "Database"
            }

            mongoDb = container "MongoDB Database" \
                "Stores IoT telemetry data, vital-sign time series, and event streams." \
                "MongoDB" {
                tags "Database"
            }

            edgeApp = container "Veyra Edge Application" \
                "Processes and filters raw sensor data in real time at the nursing home edge before forwarding alerts." \
                "Node.js / Real-time Processing"

            embeddedVital = container "Vital Signs Embedded App" \
                "Firmware running on the vital-signs monitoring device. Collects heart rate, SpO2, temperature, and blood pressure." \
                "C++ Firmware"

            embeddedGPS = container "GPS Embedded App" \
                "Firmware running on the GPS tracker device. Collects and transmits patient location data." \
                "C++ Firmware"

            webApp        -> webClient  "Delivers SPA bundle to"                "HTTPS"
            webClient     -> api        "Makes API requests to"                 "HTTPS / JSON"
            mobileApp     -> api        "Makes API requests to"                 "HTTPS / JSON"
            api           -> mysqlDb    "Reads from and writes to"              "TCP / MySQL protocol"
            api           -> mongoDb    "Reads from and writes to"              "TCP / MongoDB protocol"
            embeddedVital -> edgeApp    "Streams raw vital-sign data to"        "MQTT / TCP"
            embeddedGPS   -> edgeApp    "Streams raw GPS location data to"      "MQTT / TCP"
            edgeApp       -> api        "Forwards processed data and alerts to" "HTTPS / JSON"
        }

        deploymentEnvironment "Production" {

            deploymentNode "Veyra Production Infrastructure" "" "Global Multi-Cloud and On-Premises Environment" {
                tags "Wrapper"

                deploymentNode "Cloudflare" "" "CDN / Edge Network" {
                    tags "Cloudflare"

                    deploymentNode "Cloudflare Pages - Landing" "" "Static Hosting" {
                        containerInstance veyra.landingPage
                    }

                    deploymentNode "Cloudflare Pages - Web App" "" "Static Hosting" {
                        webAppInstance = containerInstance veyra.webApp
                    }
                }

                deploymentNode "Google Firebase" "" "Mobile Platform" {
                    tags "Firebase"

                    deploymentNode "Firebase App Distribution" "" "App Distribution Service" {
                        containerInstance veyra.mobileApp
                    }
                }

                deploymentNode "User Device" "" "Microsoft Windows, macOS, or Linux" {
                    tags "UserDevice"

                    deploymentNode "Web Browser" "" "Chrome, Firefox, Safari, or Edge" {
                        webClientInstance = containerInstance veyra.webClient
                    }
                }

                deploymentNode "Microsoft Azure" "" "Cloud Provider" {
                    tags "Azure"

                    deploymentNode "Azure App Service Plan" "" "PaaS - Auto-scaling Compute" {

                        deploymentNode "App Service Instance" "" "Azure App Service" {
                            apiInstance = containerInstance veyra.api
                        }
                    }

                    deploymentNode "Azure Database Services" "" "Managed Database Platform" {

                        deploymentNode "Azure Database for MySQL - Flexible Server" "" "Managed Relational Database" {
                            mysqlInstance = containerInstance veyra.mysqlDb
                        }

                        deploymentNode "Azure Cosmos DB (MongoDB API)" "" "Managed NoSQL Service - Serverless" {
                            mongoInstance = containerInstance veyra.mongoDb
                        }
                    }
                }

                deploymentNode "Nursing Home Environment" "" "On-premises Local Network" {
                    tags "OnPremises"

                    deploymentNode "Edge Server" "" "Linux - ARM64 / x86_64" {
                        edgeInstance = containerInstance veyra.edgeApp
                    }

                    deploymentNode "Vital Signs IoT Device" "" "Embedded Hardware - ARM Cortex-M" {
                        containerInstance veyra.embeddedVital
                    }

                    deploymentNode "GPS Tracker Device" "" "Embedded Hardware - ARM Cortex-M" {
                        containerInstance veyra.embeddedGPS
                    }
                }
            }
        }
    }

    views {

        deployment veyra "Production" "Veyra-Platform-Production-Deployment" {
            include *
            autoLayout lr
            title "Veyra Platform - Production Deployment"
            description "C4 Model - Deployment View - Production Environment"
        }

        styles {

            element "Container" {
                background #438dd5
                color #ffffff
                shape RoundedBox
                border solid
            }

            element "Database" {
                background #438dd5
                color #ffffff
                shape Cylinder
                border solid
            }

            element "Infrastructure Node" {
                background #85bbf0
                color #000000
                shape RoundedBox
                border solid
            }

            element "Deployment Node" {
                background #ffffff
                color #000000
                stroke #999999
                border dashed
            }

            element "Wrapper" {
                background #f8f8f8
                color #333333
                stroke #333333
                border dashed
            }

            element "Cloudflare" {
                background #fff7ed
                stroke #f97316
                color #7c2d12
                border dashed
            }

            element "Azure" {
                background #eff6ff
                stroke #3b82f6
                color #1e3a8a
                border dashed
            }

            element "Firebase" {
                background #fefce8
                stroke #eab308
                color #713f12
                border dashed
            }

            element "OnPremises" {
                background #f0fdf4
                stroke #16a34a
                color #14532d
                border dashed
            }

            element "UserDevice" {
                background #faf5ff
                stroke #9333ea
                color #4c1d95
                border dashed
            }

            relationship "Relationship" {
                dashed true
                color #707070
                fontSize 10
                thickness 2
            }
        }
    }
}