# Knowtify - Quick Start Guide

## 🎯 App Now Running at http://localhost:8080

### First Time Setup (2 minutes)

1. **Open your browser**: http://localhost:8080
2. **Click "Get Started"** on the landing page
3. **Sign Up** with:
   - Username: `testuser`
   - Email: `test@knowtify.com`
   - Password: `password123`
   - Full Name: `Test User`
4. **Select Domains** - Choose at least 3 domains you want to learn
5. **Set Interval** - Pick how often you want knowledge cards (default: Every 1 hour)
6. **Start Learning** - Click "Start Learning"

### Tour the App

#### 📊 Dashboard
- See your learning stats (Cards learned, Streak, Accuracy)
- Click **"Get Today's Card"** to start learning
- View progress rings for each domain

#### 📚 Knowledge Card
- **Front side**: Shows the topic question
- **Click to flip**: Reveals the complete answer and explanation
- **Mark as correct/incorrect**: Track your learning
- **Bookmark**: Save topics for later review

#### 📖 History
- View all cards you've learned
- Filter by domain
- Search for specific topics
- See if you got them right or wrong

#### 👤 Profile
- View overall statistics
- See your bookmarked topics
- Check achievements
- Update settings

### 🔑 Key Features Explained

#### No Repetition System
- **First cycle**: You get each topic in a domain once, in random order
- **When all topics done**: System automatically resets and shuffles
- **Result**: Smart learning without boring repetition

#### Progress Tracking
- **Green ring**: Percentage of topics completed in that domain
- **"10/30 topics covered"**: You've learned 10 out of 30 topics
- **Accuracy %**: Percentage of cards you marked as correct
- **Streak**: Consecutive days of learning

#### Beautiful Dark Theme
- Glassmorphic cards with semi-transparent backgrounds
- Neon purple, cyan, and pink accent colors
- Smooth 3D flip animations
- Mobile responsive design

### 💡 Tips for Best Learning

1. **Set regular intervals** - Daily learning builds better understanding
2. **Mark honestly** - Say "Not Clear" if you didn't understand
3. **Bookmark important topics** - Review them in profile
4. **Check history** - Reinforce what you've learned
5. **Complete full cycles** - Learn all topics to get the reset

### 🚀 Test the No-Repetition System

Try this to see it in action:

1. Pick "DSA" domain (has 10 topics)
2. Click "Get Today's Card" 10 times
3. You'll get each topic exactly once
4. On the 11th click, it resets and shuffles
5. Notice the progress ring fills and resets

### 📱 Mobile Usage

- Responsive design works on phones and tablets
- Tap cards to flip them
- Touch-friendly buttons and forms
- All features work the same on mobile

### 🔗 API Testing

Want to test the APIs directly?

```bash
# Get all domains
curl http://localhost:8080/api/domains

# Get topics in DSA domain
curl http://localhost:8080/api/topics/domain/1

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Get next knowledge card for user 1 in DSA
curl http://localhost:8080/api/knowledge/next?userId=1&domain=DSA
```

### 🐛 Troubleshooting

**App won't load?**
- Check if server is running: `mvn spring-boot:run`
- Ensure port 8080 is not blocked
- Clear browser cache (Ctrl+Shift+Del)

**Can't login after registration?**
- Data is in-memory H2 database
- Use exact same username/password from signup
- Browser localStorage saves login, close all tabs to fully reset

**Topics not showing?**
- Refresh page
- Check browser console (F12) for errors
- Ensure you selected domains in onboarding

**Want to reset everything?**
- Stop server: Ctrl+C in terminal
- Run: `mvn clean spring-boot:run`
- This resets the H2 database

### 📚 Database

**Admin Console**: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:knowtifydb`
- Username: `sa`
- Password: (leave blank)
- Click "Connect"

View/modify:
- `USERS` - User accounts
- `DOMAINS` - Learning domains
- `TOPICS` - Topics in domains
- `KNOWLEDGE_CARDS` - Cards shown to users
- `USER_PROGRESS` - Which topics each user has seen

### 📊 Sample Domains (Pre-loaded)

1. **DSA** - Data Structures & Algorithms (10 topics)
2. **Machine Learning** - ML concepts (10 topics)
3. **Web Development** - Frontend/Backend (10 topics)
4. **Cloud Computing** - AWS, Docker, K8s (10 topics)
5. **Artificial Intelligence** - LLMs, Vision, NLP (10 topics)
6. **Cybersecurity** - Security threats & defense (10 topics)
7. **DevOps** - CI/CD, Infrastructure (10 topics)
8. **Database** - SQL, NoSQL, Design (10 topics)
9. **System Design** - Scalability, Architecture (10 topics)
10. **Mobile Development** - iOS, Android, Flutter (10 topics)

**Total**: 100 topics ready to learn!

### 🎬 Next Steps

1. **Complete all topics** in one domain to see the reset
2. **Check your stats** in the profile
3. **Bookmark** a few topics for review
4. **Explore the API** using the endpoints above
5. **Customize** by adding more domains/topics via API

### 🚀 Going Live

When ready for production:

1. Switch to PostgreSQL database
2. Add JWT authentication
3. Implement email notifications
4. Deploy to cloud (Heroku, AWS, etc.)
5. Add more advanced features (spaced repetition, AI recommendations)

---

**Enjoy learning with Knowtify! 🎓**

Questions? Check the README.md for detailed documentation.
