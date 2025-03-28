# ImposterAI

ImposterAI is an Android chat game that challenges your ability to distinguish between human and AI interactions. In this modern twist on the Turing test, you'll engage in a 2-minute conversation with an opponent who could be either a real human or an AI chatbot.

## 🎮 Game Concept

The premise is simple yet captivating:

1. **Matchmaking**: The app pairs you with an opponent - either a real human player or an AI.
2. **Conversation**: You have a 2-minute chat session with your matched opponent.
3. **Decision Time**: When time runs out, you must decide if you were talking to a human or an AI.
4. **Results**: Find out if your guess was correct and see your success rate over time.

## 🌟 Features

- **Real-time Matchmaking**: Connect with other players or AI opponents.
- **WebSocket Chat**: Smooth, real-time messaging experience.
- **Timer System**: Each game lasts exactly 2 minutes to keep sessions concise.
- **Authentication**: User accounts to track your game history and performance.
- **Modern UI**: Clean, intuitive interface built with Jetpack Compose.

## 🧠 The Challenge

The game explores an increasingly relevant question: As AI becomes more sophisticated, can we tell when we're interacting with a machine? ImposterAI puts your perception to the test in a fun and competitive format.

## 🛠️ Tech Stack

- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Clean separation of concerns
- **Dagger-Hilt** - Dependency injection
- **Retrofit** - HTTP client for API communication
- **WebSockets** - For real-time communication
- **Kotlin Flow** - Reactive programming
- **Kotlin Serialization** - For parsing JSON
- **Firebase** - Remote config and analytics

## 🏗️ Architecture

The project follows clean architecture principles with:

- **Presentation Layer**: UI components and ViewModels
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Repositories, data sources, and models

### Key Components:

- **WebSocketService**: Handles real-time communication for chatting
- **GameViewModel**: Manages game state and communication
- **AuthRepository**: Handles user authentication
- **Navigation**: Uses Jetpack Navigation for seamless screen transitions

## 📱 Screens

- **Login/Signup**: User authentication
- **Home**: Game explanation and start button
- **Matchmaking**: Finding an opponent
- **Chat**: The main game interface
- **Result**: Shows whether your guess was correct

## 🚀 System Requirements

- Android device running API 24 (Android 7.0) or higher
- Internet connection for matchmaking and real-time chat

## 🌐 Backend Requirements

The app requires a backend server that provides:

- Authentication endpoints
- WebSocket server for real-time chatting
- Match-making service
- AI opponent implementation

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 👏 Acknowledgements

- The concept was inspired by the classic Turing test and modern AI capabilities
- Thanks to all testers who helped refine the gameplay experience