# Knowtify - Smart Knowledge Notification System

A premium full-stack web application that delivers bite-sized knowledge cards across 10 learning domains with **zero topic repetition** until all topics are covered. Features a stunning dark glassmorphism UI with neon accents and smooth animations.

## 🚀 Features

### Core Features
- ✅ **No Repetition System**: Each topic shown once until all are covered, then shuffles and resets
- 📚 **10 Learning Domains**: DSA, ML, WebDev, Cloud, AI, Security, DevOps, Database, System Design, Mobile Dev
- 🎯 **100+ Topics**: ~10 topics per domain with difficulty levels
- ⏰ **Personalized Intervals**: Choose notification frequency (15min to 24hrs)
- 🔖 **Bookmarking**: Save topics for later review
- 📊 **Progress Tracking**: Visual progress rings and domain statistics
- 🔥 **Streak System**: Track learning consistency
- 💾 **History Timeline**: Review all learned cards with filters

### UI/UX
- 🎨 **Dark Glassmorphism Theme**: Premium look with backdrop blur
- 🌈 **Neon Accents**: Purple, Cyan, Pink gradients
- ✨ **Smooth Animations**: Float, flip, fade, pulse effects
- 📱 **Mobile Responsive**: Works perfectly on all devices
- 🎬 **3D Card Flip**: Interactive knowledge card reveal
- 🎯 **Micro-interactions**: Toast notifications, loading spinners, transitions

## 📋 Tech Stack

### Backend
- **Spring Boot 3.2.0** - REST APIs and core logic
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory with file persistence
- **Lombok** - Reduce boilerplate
- **Maven** - Dependency management

### Frontend
- **HTML5** - Semantic structure
- **CSS3** - Glassmorphism, gradients, animations
- **Vanilla JavaScript** - No frameworks, pure ES6+
- **Google Fonts** - Inter & Space Grotesk

## 🏗️ Architecture

### Project Structure
```
Knowtify_application/
├── src/main/java/com/knowtify/
│   ├── KnowtifyApplication.java        # Main Spring Boot app
│   ├── controller/
│   │   ├── AuthController.java         # Auth endpoints
│   │   ├── DomainController.java       # Domain CRUD
│   │   ├── TopicController.java        # Topic CRUD
│   │   ├── KnowledgeController.java    # Knowledge cards
│   │   └── UserController.java         # User preferences
│   ├── service/
│   │   ├── DomainService.java
│   │   ├── TopicService.java
│   │   ├── UserService.java
│   │   ├── UserProgressService.java
│   │   └── KnowledgeService.java       # Core no-repetition logic
│   ├── entity/
│   │   ├── User.java
│   │   ├── Domain.java
│   │   ├── Topic.java
│   │   ├── Knowledge.java
│   │   └── UserProgress.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── DomainRepository.java
│   │   ├── TopicRepository.java
│   │   ├── KnowledgeRepository.java
│   │   └── UserProgressRepository.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── AuthResponse.java
│   │   ├── AnswerRequest.java
│   │   └── PreferencesRequest.java
│   └── util/
│       └── DataInitializer.java        # Auto-load sample data
├── src/main/resources/
│   ├── application.properties          # Config
│   └── static/
│       ├── index.html                  # Main app
│       ├── style.css                   # Dark theme
│       └── app.js                      # App logic
├── pom.xml                             # Maven dependencies
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Installation & Running

1. **Clone the repository**
   ```bash
   cd C:\Users\pujit\Knowtify_application
   ```

2. **Build the application**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the app**
   - Open browser: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`

## 📖 Pages & Workflows

### 1. Landing Page
- Hero section with animated floating card
- Feature showcase (6 key features)
- 15 domain showcase
- Call-to-action buttons

### 2. Authentication
- Register with username, email, password, full name
- Login with credentials stored in database
- Persistent login using localStorage

### 3. Onboarding
- Select preferred learning domains (multi-select)
- Set notification interval
- Initialize user preferences

### 4. Dashboard
- Quick stats: Cards learned, streak, accuracy
- "Get Today's Card" button
- Domain progress rings showing completion %
- Interactive progress visualization

### 5. Knowledge Card
- Front: Question/topic name with hint
- Back: Complete answer with explanation
- 3D flip animation on click
- Actions: Mark correct/incorrect, bookmark
- Time tracking from card load to answer
- Difficulty and domain badges

### 6. History Page
- List of all learned cards chronologically
- Filter by domain
- Search by topic name
- Shows correctness status

### 7. Profile Page
- User stats dashboard
- Bookmarked topics
- Achievements badges
- Account settings
- Logout button

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/register          - Create new user
POST   /api/auth/login             - Login user
GET    /api/auth/user/{userId}     - Get user profile
```

### Domains
```
GET    /api/domains                - List all domains
GET    /api/domains/{id}           - Get domain by ID
GET    /api/domains/name/{name}    - Get domain by name
POST   /api/domains                - Create domain
PUT    /api/domains/{id}           - Update domain
DELETE /api/domains/{id}           - Delete domain
GET    /api/domains/{id}/topic-count - Count topics in domain
```

### Topics
```
GET    /api/topics/domain/{domainId}      - Get topics by domain
GET    /api/topics/domain-name/{name}     - Get topics by domain name
GET    /api/topics/{id}                    - Get topic by ID
POST   /api/topics                         - Create topic
PUT    /api/topics/{id}                    - Update topic
DELETE /api/topics/{id}                    - Delete topic
GET    /api/topics/{domainId}/count       - Count topics in domain
```

### Knowledge Cards (Core)
```
GET    /api/knowledge/next?userId=1&domain=DSA
       - Get next knowledge card (NO-REPETITION logic)
GET    /api/knowledge/{knowledgeId}
       - Get specific card
GET    /api/knowledge/user/{userId}/history
       - Get user's card history
GET    /api/knowledge/user/{userId}/domain/{domainId}
       - Get cards by domain
POST   /api/knowledge/{knowledgeId}/answer
       - Submit answer to card
GET    /api/knowledge/user/{userId}/stats
       - Get overall user statistics
GET    /api/knowledge/user/{userId}/domain/{domainId}/stats
       - Get domain-specific statistics
```

### User Progress
```
GET    /api/user/{userId}
       - Get user profile
PUT    /api/user/{userId}/preferences
       - Update domain preferences and interval
PUT    /api/user/{userId}/stats
       - Update user statistics
POST   /api/user/{userId}/card-learned
       - Increment cards learned counter
GET    /api/user/{userId}/progress
       - Get all user progress
GET    /api/user/{userId}/completed
       - Get completed topics
GET    /api/user/{userId}/bookmarked
       - Get bookmarked topics
POST   /api/user/{userId}/topic/{topicId}/bookmark
       - Toggle bookmark on topic
POST   /api/user/{userId}/topic/{topicId}/complete
       - Mark topic as complete
GET    /api/user/{userId}/domain/{domainId}/stats
       - Get domain progress
DELETE /api/user/{userId}
       - Delete user account
```

## 🎯 Core Logic: No-Repetition System

The `KnowledgeService.getNextKnowledgeCard()` implements the no-repetition algorithm:

```java
1. Get all topics in requested domain
2. Get topics user has already viewed (from UserProgress)
3. Filter out viewed topics
4. If all topics viewed:
   - Reset domain progress (clear viewed flags)
   - Shuffle and return random topic
5. Else:
   - Return random topic from unviewed list
6. Record view in UserProgress
7. Create and return knowledge card
```

**Result**: Each user gets each topic exactly once per cycle, then the cycle resets with shuffled order.

## 🎨 Design System

### Colors
- **Background**: `#0a0a1a` (Dark Navy)
- **Card Background**: `rgba(20, 20, 40, 0.7)` (Glassmorphism)
- **Accent Purple**: `#7c3aed` (Primary action)
- **Accent Cyan**: `#06b6d4` (Success/Info)
- **Accent Pink**: `#ec4899` (Secondary action)
- **Text Primary**: `#ffffff`
- **Text Secondary**: `#a0a0b8`

### Typography
- **Display**: Space Grotesk (headings)
- **Body**: Inter (text)
- **Weights**: 300, 400, 500, 600, 700

### Components
- **Cards**: Glassmorphic with 1px border, backdrop blur(10px)
- **Buttons**: Gradient backgrounds, smooth hover transitions
- **Inputs**: Transparent background, cyan border on focus
- **Progress Bars**: Gradient fills with smooth animations

## 📊 Database Schema

### Tables
- `users` - User accounts with preferences
- `domains` - Learning domains (DSA, ML, etc.)
- `topics` - Individual topics within domains
- `knowledge_cards` - Cards presented to users
- `user_progress` - Tracks which topics user has viewed/completed
- `user_domains` - User's preferred domains (collection table)

### Key Relationships
- User → Domain (many-to-many)
- User → UserProgress → Topic (tracks which topics shown)
- User → Knowledge Cards
- Domain → Topic (one-to-many)

## 🔐 Authentication & Security

- Simple username/password authentication (localStorage based)
- Each user has isolated progress
- User-specific content served based on userId
- CORS enabled for localhost
- No sensitive data in localStorage (could use JWT in production)

## 🚀 Performance & Scalability

- H2 in-memory database with file persistence
- JPA query optimization with proper indexes
- Lazy loading on relationships to reduce memory
- Frontend: Pure Vanilla JS (no framework overhead)
- CSS animations use GPU acceleration
- Minimal JavaScript bundle

## 📝 Sample Data

Auto-initialized on first run with:
- **10 Learning Domains** with descriptions and color codes
- **100 Topics** (~10 per domain) with:
  - Name and description
  - Content/explanation
  - Difficulty level (Beginner/Intermediate/Advanced)
  - Key points and examples

## 🛠️ Development

### Adding New Domain
1. Create Domain via API or DataInitializer
2. Add Topics to domain
3. Topics automatically appear in domain progress

### Customizing Topics
Edit `DataInitializer.java` to modify sample topics or create via API.

### Styling
- All CSS in `style.css`
- CSS variables defined in `:root`
- Responsive breakpoints at 768px

### Frontend Logic
- `app.js` handles all functionality
- No external dependencies
- Clear separation: UI (HTML), Style (CSS), Logic (JS)

## 🐛 Known Limitations

- Simple authentication (no password encryption in demo)
- H2 database resets on app restart if not configured for persistence
- Single-threaded (no concurrent user handling)
- No email notifications (UI placeholder)

## 🚀 Production Considerations

- Use PostgreSQL/MySQL instead of H2
- Implement JWT authentication
- Add password hashing (bcrypt)
- Enable HTTPS
- Add request rate limiting
- Implement caching (Redis)
- Add monitoring and logging
- Deploy to cloud (AWS, GCP, Azure)

## 📞 Support

For issues or questions, check:
1. Console logs in browser (F12)
2. Server logs in terminal
3. H2 console at `/h2-console`
4. API responses in Network tab

## 📄 License

Created for Knowtify - Smart Knowledge Notification System

---

**Built with ❤️ using Spring Boot, Vanilla JS, and CSS Glassmorphism**
