# AGI Event Networking App

> An Android application designed to make event participation more connected, organized, and engaging.

## Overview

The **AGI Event Networking App** is a mobile platform for event attendees and organizers. It combines event information, attendee interaction, real-time communication, and notifications in one streamlined experience.

The project demonstrates practical experience building an Android application backed by Firebase, with a focus on authentication, cloud data management, real-time collaboration, and intuitive mobile navigation.

## Key Features

- **Attendee registration and authentication** — Supports secure user sign-up and sign-in through Firebase Authentication.
- **Real-time chat** — Enables attendees to communicate through chat rooms and messages.
- **Event content and updates** — Provides access to event posts, agendas, and announcements.
- **Notifications** — Delivers updates to users and supports notification read status management.
- **User profiles** — Stores attendee information and supports user-specific access controls.
- **Commenting and engagement** — Allows authenticated users to create and manage comments.
- **Simple mobile navigation** — Organizes core experiences into an accessible Android interface.

## Technology Stack

### Mobile Application

- **Java** — Primary application language.
- **Android SDK** — Native Android development platform.
- **XML layouts** — Defines responsive mobile user interfaces.
- **AndroidX, Material Components, and ConstraintLayout** — Supports modern UI patterns and flexible screen layouts.
- **Gradle Kotlin DSL** — Manages builds, plugins, and dependencies.

### Backend and Data

- **Firebase Authentication** — Handles attendee identity and access.
- **Cloud Firestore** — Provides scalable document storage and real-time data synchronization.
- **Firebase Cloud Messaging** — Supports push notifications and event updates.
- **Firebase Storage** — Supports cloud-based media and asset storage.
- **Firestore Security Rules** — Applies authentication-based and role-based access controls for users, chats, agendas, comments, and notifications.

## Architecture Highlights

- Native Android application structure with a dedicated `app` module.
- Cloud-backed data model using Firestore collections for users, posts, comments, chat rooms, agendas, and notifications.
- Authenticated access throughout the application.
- Administrator controls for managing agenda content and creating notifications.
- User-specific permissions for profiles, comments, and notification updates.
- Android API configuration targeting SDK 35, with support for devices from API 24 onward.

## Challenges and Problem-Solving

### Real-time data synchronization

Synchronizing Firestore data while keeping the interface responsive required careful handling of asynchronous operations and update timing. This provided hands-on experience with cloud-backed application state and real-time data flows.

### Multi-screen navigation

Connecting multiple application screens and ensuring users could move through registration, event content, communication, and notifications presented navigation and state-management challenges. Resolving these issues strengthened the overall usability of the application.

### Development environment and Firebase configuration

Android Studio performance issues and intermittent Firebase connectivity slowed development. Troubleshooting configuration, build dependencies, and service integration improved the development workflow and reinforced the importance of systematic debugging.

## Lessons Learned

- Implemented Firebase Authentication for user access and identity management.
- Gained practical experience designing Firestore data structures and security rules.
- Improved understanding of asynchronous operations and real-time data synchronization.
- Strengthened debugging skills across Android Studio, Gradle, and Firebase services.
- Learned to design mobile workflows around clear navigation and user engagement.

## Getting Started

### Prerequisites

- Android Studio with Android SDK support.
- Java 11 or a compatible JDK.
- An Android emulator or physical Android device.
- A Firebase project configured for an Android application.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/96as/AGI-event-networking-app.git
   cd AGI-event-networking-app
   ```

2. Open the project in Android Studio.
3. Connect the project to a Firebase project and add the appropriate `google-services.json` file to the `app/` directory.
4. Confirm that Firebase Authentication, Cloud Firestore, Cloud Messaging, and Storage are enabled as required.
5. Sync the Gradle project.
6. Build and run the application on an emulator or connected Android device.

## Project Status

This project is a functional Android application prototype demonstrating event management and networking workflows. Future improvements could include automated testing coverage, refined offline support, stronger validation and error states, accessibility enhancements, and expanded administrator tooling.

## What This Project Demonstrates

- Native Android development with Java and XML.
- Integration of multiple Firebase services.
- Authentication and role-based authorization.
- Real-time communication and cloud data synchronization.
- Mobile UI design and navigation.
- Debugging and problem-solving in a multi-service development environment.

## Author

Built by **96as** as an Android event networking project.

For more information, implementation details, or a walkthrough of the application, please explore the source code or get in touch through GitHub.
