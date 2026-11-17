package com.example.data.repository

import com.example.data.local.AttemptDao
import com.example.data.local.QuestionDao
import com.example.data.model.Attempt
import com.example.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class QuizRepository(
    private val questionDao: QuestionDao,
    private val attemptDao: AttemptDao
) {
    val allQuestions: Flow<List<Question>> = questionDao.getAllQuestions()
    val allAttempts: Flow<List<Attempt>> = attemptDao.getAllAttempts()

    fun getQuestionsBySubject(subject: String): Flow<List<Question>> {
        val canonical = when (subject.trim()) {
            "English", "الإنجليزية" -> "English"
            "Mathematics", "Math", "الرياضيات" -> "Mathematics"
            "Science", "العلوم" -> "Science"
            "Social Studies", "الدراسات الاجتماعية" -> "Social Studies"
            "Arabic", "اللغة العربية" -> "اللغة العربية"
            "Islamic Studies", "التربية الإسلامية" -> "Islamic Studies"
            else -> subject
        }
        return questionDao.getQuestionsBySubject(canonical)
    }

    suspend fun insertQuestion(question: Question) = withContext(Dispatchers.IO) {
        questionDao.insertQuestion(question)
    }

    suspend fun deleteQuestionById(id: Long) = withContext(Dispatchers.IO) {
        questionDao.deleteQuestionById(id)
    }

    suspend fun insertAttempt(attempt: Attempt) = withContext(Dispatchers.IO) {
        attemptDao.insertAttempt(attempt)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        attemptDao.clearAllAttempts()
    }

    suspend fun preloadDefaultQuestionsIfEmpty() = withContext(Dispatchers.IO) {
        val currentList = questionDao.getAllQuestions().first()
        val hasLegacyBadQuestions = currentList.any {
            it.subject == "الرياضيات" || it.subject == "التربية الإسلامية" || it.subject == "الدراسات الاجتماعية" ||
                (it.subject == "Mathematics" && it.questionText.contains("عد الفراوشات")) ||
                (it.subject == "Mathematics" && it.correctAnswer == "1 - 2 - 3 - 4") ||
                (it.subject == "اللغة العربية" && it.correctAnswer == "أَسَد")
        }

        val defaultQuestions = buildDefaultQuestions()

        if (currentList.isEmpty() || hasLegacyBadQuestions) {
            questionDao.deleteAllQuestions()
            questionDao.insertQuestions(defaultQuestions)
        } else {
            // Insert any newly added curriculum questions without deleting user-created custom questions
            val existingKeys = currentList.map { it.subject to it.questionText }.toSet()
            val missingDefaults = defaultQuestions.filter { (it.subject to it.questionText) !in existingKeys }
            if (missingDefaults.isNotEmpty()) {
                questionDao.insertQuestions(missingDefaults)
            }
        }
    }

    private fun buildDefaultQuestions(): List<Question> = listOf(
        // ========== ARABIC (اللغة العربية) - 10 Questions ==========
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الأولى: الحروف الهجائية",
            questionType = "MULTIPLE_CHOICE",
            questionText = "أي من الكلمات التالية تبدأ بحرف (أ)؟",
            hintImage = "🐰",
            options = "أَرْنَب;بَيْت;كِتَاب;تُفَّاحَة",
            correctAnswer = "أَرْنَب",
            explanation = "كلمة (أَرْنَب) تبدأ بـحرف الألف المفتوح (أَ).",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الأولى: الحروف الهجائية",
            questionType = "FILL_BLANK",
            questionText = "أكمل الحرف الناقص في اسم هذه الصورة الجميلة: بـ...ـت",
            hintImage = "🏠",
            options = "ي;و;أ;ش",
            correctAnswer = "ي",
            explanation = "الحرف الناقص هو الياء لتصبح الكلمة (بَيْت).",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثانية: تكوين الكلمات",
            questionType = "ARRANGE_LETTERS",
            questionText = "رتب الحروف المبعثرة سريعاً لتكوين اسم ملك الغابة!",
            hintImage = "🦁",
            options = "س;أ;د",
            correctAnswer = "أسد",
            explanation = "الحروف (أ - س - د) تكون كلمة (أَسَد) ملك الغابة المبهج!",
            difficulty = "متوسط"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثانية: تكوين الكلمات",
            questionType = "IMAGE_SELECTION",
            questionText = "اختر صورة (الـسَّـمَـكَـة) الملونة من الخيارات أسفله:",
            hintImage = "🌊",
            options = "🐟;🐪;🍎;🚗",
            correctAnswer = "🐟",
            explanation = "السمكة تعيش في الماء وهي ملونة ومرحة!",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثالثة: المدود والحركات",
            questionType = "MATCH_COLUMNS",
            questionText = "صل كل حرف بالكلمة المناسبة له:",
            hintImage = "📚",
            options = "أ-أرنب;ب-بطة;ت-تفاحة;ث-ثعلب",
            correctAnswer = "أ-أرنب;ب-بطة;ت-تفاحة;ث-ثعلب",
            explanation = "الحروف تبدأ بها الكلمات: أ لأرنب، ب لبطة، ت لتفاحة، وث لثعلب! مذهل!",
            difficulty = "متوسط"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثالثة: المدود والحركات",
            questionType = "TRUE_FALSE",
            questionText = "المد في كلمة (تُـفَّـاحَـة) هو مد بالواو.",
            hintImage = "🍎",
            options = "صح;خطأ",
            correctAnswer = "خطأ",
            explanation = "المد في كلمة (تُـفَّـاحَـة) هو مد بالألِف وليس بالواو.",
            difficulty = "متوسط"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثانية: تكوين الكلمات",
            questionType = "ARRANGE_LETTERS",
            questionText = "رتب الحروف لتكوين اسم الجسم المنير ليلاً في السماء:",
            hintImage = "🌙",
            options = "م;ق;ر",
            correctAnswer = "قمر",
            explanation = "الحروف (ق - م - ر) تكون كلمة (قَمَر) الجميلة!",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الأولى: الحروف الهجائية",
            questionType = "MULTIPLE_CHOICE",
            questionText = "ما هو الحرف الذي يأتي بعد حرف (ج) في الحروف الهجائية؟",
            hintImage = "🔤",
            options = "ح;خ;ب;د",
            correctAnswer = "ح",
            explanation = "ترتيب الحروف هو: أ، ب، ت، ث، ج، ح، خ.",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الثالثة: المدود والحركات",
            questionType = "FILL_BLANK",
            questionText = "أكمل الحرف الناقص في كلمة: شَـ...ـس",
            hintImage = "☀️",
            options = "م;ل;ك;ع",
            correctAnswer = "م",
            explanation = "الحرف الناقص هو الميم لتصبح الكلمة (شَمْس).",
            difficulty = "سهل"
        ),
        Question(
            subject = "اللغة العربية",
            unit = "الوحدة الرابعة: المفردات الجميلة",
            questionType = "TRUE_FALSE",
            questionText = "كلمة (قَلَم) تبدأ بحرف القاف.",
            hintImage = "✏️",
            options = "صح;خطأ",
            correctAnswer = "صح",
            explanation = "نعم، كلمة (قَلَم) تبدأ بحرف القاف المفتوح (قَ).",
            difficulty = "سهل"
        ),

        // ========== MATHEMATICS (In English) - 10 Questions ==========
        Question(
            subject = "Mathematics",
            unit = "Unit 1: Numbers & Counting",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Count the beautiful butterflies in the garden: 🦋 🦋 🦋 🦋",
            hintImage = "🌸",
            options = "2;3;4;5",
            correctAnswer = "4",
            explanation = "There are 4 colorful butterflies flying in the garden!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 2: Addition & Subtraction",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What is the answer: 3 + 2 = ?",
            hintImage = "➕",
            options = "3;4;5;6",
            correctAnswer = "5",
            explanation = "3 apples plus 2 apples makes 5 delicious apples!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 2: Addition & Subtraction",
            questionType = "FILL_BLANK",
            questionText = "Fill in the missing number: 7 - ... = 4",
            hintImage = "🔢",
            options = "1;2;3;4",
            correctAnswer = "3",
            explanation = "When subtracting 3 from 7, we have 4 left. Awesome!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 1: Numbers & Counting",
            questionType = "ARRANGE_LETTERS",
            questionText = "Arrange the numbers from smallest to largest:",
            hintImage = "📈",
            options = "3;1;2;4",
            correctAnswer = "1234",
            explanation = "The correct ascending order is: 1, then 2, then 3, then 4.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 3: Patterns & Shapes",
            questionType = "IMAGE_SELECTION",
            questionText = "Which shape completes the pattern: 🔴 🔵 🔴 🔵 ... ?",
            hintImage = "🎨",
            options = "🔴;🔵;🟡;🟢",
            correctAnswer = "🔴",
            explanation = "The pattern alternates between red and blue circles, so red is next!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 3: Shapes & Geometry",
            questionType = "TRUE_FALSE",
            questionText = "A triangle has 3 corners and 3 sides: 🔺.",
            hintImage = "📐",
            options = "True;False",
            correctAnswer = "True",
            explanation = "That is correct! A triangle always has 3 sides and 3 corners.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 1: Numbers & Counting",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which number is greater: 9 or 6?",
            hintImage = "⭐",
            options = "9;6;Equal",
            correctAnswer = "9",
            explanation = "9 is greater than 6!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 2: Addition & Subtraction",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Sarah has 4 stars ⭐ and gets 4 more ⭐. How many stars does she have?",
            hintImage = "🌟",
            options = "6;7;8;9",
            correctAnswer = "8",
            explanation = "4 + 4 = 8 stars! Double fun!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 3: Shapes & Geometry",
            questionType = "MATCH_COLUMNS",
            questionText = "Match each math problem to its correct answer:",
            hintImage = "🧮",
            options = "1 + 1-2;2 + 3-5;6 - 2-4;5 + 5-10",
            correctAnswer = "1 + 1-2;2 + 3-5;6 - 2-4;5 + 5-10",
            explanation = "1+1=2, 2+3=5, 6-2=4, and 5+5=10. Great math skills!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Mathematics",
            unit = "Unit 4: Telling Time",
            questionType = "TRUE_FALSE",
            questionText = "A square has 4 equal sides: 🟦.",
            hintImage = "🟦",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Yes! Every square has 4 equal sides and 4 corners.",
            difficulty = "Easy"
        ),

        // ========== ISLAMIC STUDIES (In English) - 10 Questions ==========
        Question(
            subject = "Islamic Studies",
            unit = "Unit 1: Pillars of Islam",
            questionType = "MULTIPLE_CHOICE",
            questionText = "How many pillars of Islam are there?",
            hintImage = "🕋",
            options = "3;5;6;7",
            correctAnswer = "5",
            explanation = "There are five pillars of Islam: Shahadah, Salah, Zakah, Sawm, and Hajj.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 2: Islamic Manners & Ethics",
            questionType = "TRUE_FALSE",
            questionText = "We say 'Bismillah' before we begin eating or drinking.",
            hintImage = "🥛",
            options = "True;False",
            correctAnswer = "True",
            explanation = "We mention the name of Allah before eating and drinking to receive blessings.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 2: Islamic Manners & Ethics",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What greeting do we say to our friends and teachers?",
            hintImage = "🤝",
            options = "Assalamu Alaikum;Good night;Goodbye;See you",
            correctAnswer = "Assalamu Alaikum",
            explanation = "The greeting of peace in Islam is Assalamu Alaikum wa Rahmatullahi wa Barakatuh.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 3: Prophet Muhammad (PBUH)",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What was the name of Prophet Muhammad's father?",
            hintImage = "✨",
            options = "Abdullah;Abu Talib;Abdul Muttalib;Hamzah",
            correctAnswer = "Abdullah",
            explanation = "Our beloved Prophet is Muhammad son of Abdullah (peace be upon him).",
            difficulty = "Medium"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 2: Islamic Manners & Ethics",
            questionType = "TRUE_FALSE",
            questionText = "Helping our parents and respecting elders is a noble deed in Islam.",
            hintImage = "👵",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Prophet Muhammad (PBUH) taught us to respect our elders and show mercy to our youngsters.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 1: Prayers & Worship",
            questionType = "MULTIPLE_CHOICE",
            questionText = "How many obligatory daily prayers (Salah) do Muslims perform?",
            hintImage = "🕌",
            options = "3;4;5;6",
            correctAnswer = "5",
            explanation = "Muslims perform 5 daily prayers: Fajr, Dhuhr, Asr, Maghrib, and Isha.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 1: The Holy Quran",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What is the Holy Book revealed by Allah to Prophet Muhammad (PBUH)?",
            hintImage = "📖",
            options = "The Holy Quran;The Torah;The Gospel;The Psalms",
            correctAnswer = "The Holy Quran",
            explanation = "The Holy Quran is the divine book of Allah revealed to Prophet Muhammad.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 2: Islamic Manners & Ethics",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What do we say after we finish eating?",
            hintImage = "🍽️",
            options = "Alhamdulillah;Good morning;Excuse me;Sorry",
            correctAnswer = "Alhamdulillah",
            explanation = "We thank Allah by saying Alhamdulillah after finishing our meal.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 3: Prophet Muhammad (PBUH)",
            questionType = "MATCH_COLUMNS",
            questionText = "Match each Islamic phrase to when we say it:",
            hintImage = "🤲",
            options = "Bismillah-Before eating;Alhamdulillah-After eating;Assalamu Alaikum-Greeting others;JazakAllahu Khairan-Thanking someone",
            correctAnswer = "Bismillah-Before eating;Alhamdulillah-After eating;Assalamu Alaikum-Greeting others;JazakAllahu Khairan-Thanking someone",
            explanation = "Beautiful Islamic phrases bring peace, gratitude, and blessings to our day!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Islamic Studies",
            unit = "Unit 1: The Holy Quran",
            questionType = "FILL_BLANK",
            questionText = "The first Surah of the Holy Quran is Surah Al-...atiha.",
            hintImage = "📜",
            options = "F;B;S;M",
            correctAnswer = "F",
            explanation = "Surah Al-Fatiha (The Opening) is the very first chapter of the Holy Quran.",
            difficulty = "Easy"
        ),

        // ========== ENGLISH (In English) - 10 Questions ==========
        Question(
            subject = "English",
            unit = "Unit 1: Capital & Small Alphabets",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which uppercase letter matches the lowercase letter 'b'?",
            hintImage = "🐝",
            options = "A;B;C;D",
            correctAnswer = "B",
            explanation = "The uppercase counterpart of lowercase 'b' is 'B' for Bee!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 2: Vocabulary & Animals",
            questionType = "IMAGE_SELECTION",
            questionText = "Choose the correct emoji expressing the word (CAT):",
            hintImage = "🐾",
            options = "🐶;🐱;🦁;🦊",
            correctAnswer = "🐱",
            explanation = "The emoji with whiskers is the CAT! Meow!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 2: Vocabulary & Animals",
            questionType = "FILL_BLANK",
            questionText = "Complete the missing letter of this fruit: A _ P L E",
            hintImage = "🍎",
            options = "P;B;X;T",
            correctAnswer = "P",
            explanation = "The completed word is APPLE. Healthy and sweet!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 3: Word Ordering & Phonics",
            questionType = "ARRANGE_LETTERS",
            questionText = "Arrange letters to spell the word (SUN): U - S - N",
            hintImage = "☀️",
            options = "N;S;U",
            correctAnswer = "SUN",
            explanation = "S - U - N spells SUN which gives us warm morning light!",
            difficulty = "Medium"
        ),
        Question(
            subject = "English",
            unit = "Unit 1: Capital & Small Alphabets",
            questionType = "TRUE_FALSE",
            questionText = "The word (DOG) starts with the letter 'D'.",
            hintImage = "🐶",
            options = "True;False",
            correctAnswer = "True",
            explanation = "That is true! DOG starts with the consonant letter 'D'. Good job!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 2: Colors & Objects",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What color is a ripe banana? 🍌",
            hintImage = "🍌",
            options = "Yellow;Blue;Red;Black",
            correctAnswer = "Yellow",
            explanation = "A ripe sweet banana is bright yellow!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 3: Word Ordering & Phonics",
            questionType = "ARRANGE_LETTERS",
            questionText = "Arrange the letters to spell the buzz insect (BEE):",
            hintImage = "🐝",
            options = "E;B;E",
            correctAnswer = "BEE",
            explanation = "B - E - E spells BEE! Bees make sweet honey!",
            difficulty = "Medium"
        ),
        Question(
            subject = "English",
            unit = "Unit 2: Vocabulary & Animals",
            questionType = "MATCH_COLUMNS",
            questionText = "Match each letter to a word starting with it:",
            hintImage = "📖",
            options = "A-Apple 🍎;B-Ball ⚽;C-Car 🚗;D-Duck 🦆",
            correctAnswer = "A-Apple 🍎;B-Ball ⚽;C-Car 🚗;D-Duck 🦆",
            explanation = "A is for Apple, B is for Ball, C is for Car, and D is for Duck!",
            difficulty = "Easy"
        ),
        Question(
            subject = "English",
            unit = "Unit 1: Capital & Small Alphabets",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which of these is a vowel letter?",
            hintImage = "🔤",
            options = "A;B;K;T",
            correctAnswer = "A",
            explanation = "The 5 vowels in English are A, E, I, O, U!",
            difficulty = "Medium"
        ),
        Question(
            subject = "English",
            unit = "Unit 2: Colors & Objects",
            questionType = "TRUE_FALSE",
            questionText = "The sky on a sunny day is usually blue.",
            hintImage = "🌤️",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Yes! On a clear sunny day, the sky looks bright blue!",
            difficulty = "Easy"
        ),

        // ========== SOCIAL STUDIES (In English) - 10 Questions ==========
        Question(
            subject = "Social Studies",
            unit = "Unit 1: My School & Community",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Where do we go every day to learn reading, writing, and play with friends?",
            hintImage = "🏫",
            options = "School;Airport;Hospital;Train Station",
            correctAnswer = "School",
            explanation = "School is our learning place where we study, read, write, and play with classmates!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 2: Traffic Rules & Safety",
            questionType = "IMAGE_SELECTION",
            questionText = "Which traffic light color means 'Stop' for vehicles?",
            hintImage = "🚦",
            options = "🔴;🟢;🟡;🔵",
            correctAnswer = "🔴",
            explanation = "The red light means STOP, yellow means GET READY, and green means GO safely!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 3: Transportation",
            questionType = "TRUE_FALSE",
            questionText = "An airplane is an air vehicle that flies high in the sky.",
            hintImage = "✈️",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Yes, airplanes fly high in the air carrying travelers quickly across long distances.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 4: Our Environment & Earth",
            questionType = "FILL_BLANK",
            questionText = "We keep our parks clean by putting trash in the dust...in.",
            hintImage = "🌳",
            options = "b;p;d;m",
            correctAnswer = "b",
            explanation = "We put trash in the dustbin to keep our environment tidy and clean!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 1: Community Helpers",
            questionType = "MATCH_COLUMNS",
            questionText = "Match each community helper to their role:",
            hintImage = "👨‍⚕️",
            options = "Teacher-Teaches students;Doctor-Treats patients;Police Officer-Keeps us safe;Firefighter-Extinguishes fires",
            correctAnswer = "Teacher-Teaches students;Doctor-Treats patients;Police Officer-Keeps us safe;Firefighter-Extinguishes fires",
            explanation = "All community helpers work hard with dedication to keep our society safe and happy!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 4: Planet Earth",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What is the name of our beautiful blue home planet?",
            hintImage = "🌍",
            options = "Earth;Mars;Jupiter;The Moon",
            correctAnswer = "Earth",
            explanation = "We live on planet Earth, filled with oceans, land, fresh air, and life!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 3: Transportation",
            questionType = "IMAGE_SELECTION",
            questionText = "Which vehicle travels on water across seas and oceans?",
            hintImage = "🌊",
            options = "🚢;🚌;🚲;🚆",
            correctAnswer = "🚢",
            explanation = "A ship travels across water on seas and oceans!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 2: Traffic Rules & Safety",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Where should pedestrians safely cross a busy street?",
            hintImage = "🚶",
            options = "Zebra Crossing;Middle of highway;Behind parked cars;Anywhere",
            correctAnswer = "Zebra Crossing",
            explanation = "Always use the pedestrian zebra crossing and look both ways before crossing!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 4: Seasons & Nature",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which season has blooming flowers and warm gentle weather?",
            hintImage = "🌸",
            options = "Spring;Winter;Midnight;Storm",
            correctAnswer = "Spring",
            explanation = "Spring is the season when flowers bloom and trees turn lush green!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Social Studies",
            unit = "Unit 1: My Family & Home",
            questionType = "TRUE_FALSE",
            questionText = "Planting trees helps keep our air fresh and clean.",
            hintImage = "🌲",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Trees give us clean oxygen and shade, making Earth healthier!",
            difficulty = "Easy"
        ),

        // ========== SCIENCE (In English) - 10 Questions ==========
        Question(
            subject = "Science",
            unit = "Unit 1: Living & Non-Living Things",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which of these is a living thing that grows, breathes, and needs water?",
            hintImage = "🌱",
            options = "A green plant;A toy car;A rock;A wooden chair",
            correctAnswer = "A green plant",
            explanation = "Plants are living things! They need water, sunlight, and air to grow.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 2: Five Senses",
            questionType = "MULTIPLE_CHOICE",
            questionText = "Which body part do we use to see colorful rainbows?",
            hintImage = "🌈",
            options = "Eyes;Ears;Hands;Nose",
            correctAnswer = "Eyes",
            explanation = "We use our eyes for the sense of sight to see colors and shapes!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 2: Five Senses",
            questionType = "IMAGE_SELECTION",
            questionText = "Which sense organ helps us listen to sweet music and birds?",
            hintImage = "🎶",
            options = "👂;👀;👃;👅",
            correctAnswer = "👂",
            explanation = "Our ears help us hear sounds all around us!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 3: Plants & Animals",
            questionType = "TRUE_FALSE",
            questionText = "Plants need sunlight and water to grow healthy and strong.",
            hintImage = "🌻",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Yes! Sunlight, water, air, and soil help plants grow tall and green.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 3: Plants & Animals",
            questionType = "FILL_BLANK",
            questionText = "Fish breathe underwater using their ...ills.",
            hintImage = "🐟",
            options = "g;w;b;t",
            correctAnswer = "g",
            explanation = "Fish use gills to breathe oxygen dissolved in water!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Science",
            unit = "Unit 4: Weather & Sky",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What bright star gives Earth light and warmth during the day?",
            hintImage = "☀️",
            options = "The Sun;The Moon;A Cloud;A Comet",
            correctAnswer = "The Sun",
            explanation = "The Sun is a bright star that lights up and warms our planet Earth!",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 4: Matter & Water",
            questionType = "TRUE_FALSE",
            questionText = "Ice is solid water that melts when it gets warm.",
            hintImage = "🧊",
            options = "True;False",
            correctAnswer = "True",
            explanation = "Correct! When ice warms up, it melts back into liquid water.",
            difficulty = "Easy"
        ),
        Question(
            subject = "Science",
            unit = "Unit 2: Five Senses",
            questionType = "MATCH_COLUMNS",
            questionText = "Match each sense organ to what it helps us do:",
            hintImage = "🔬",
            options = "Eyes 👀-Seeing colors;Ears 👂-Hearing sounds;Nose 👃-Smelling flowers;Tongue 👅-Tasting fruit",
            correctAnswer = "Eyes 👀-Seeing colors;Ears 👂-Hearing sounds;Nose 👃-Smelling flowers;Tongue 👅-Tasting fruit",
            explanation = "Our five senses help us explore and understand the wonderful world!",
            difficulty = "Medium"
        ),
        Question(
            subject = "Science",
            unit = "Unit 3: Plants & Animals",
            questionType = "ARRANGE_LETTERS",
            questionText = "Arrange the letters to spell the part of a plant that grows under the soil (ROOT):",
            hintImage = "🌿",
            options = "O;R;T;O",
            correctAnswer = "ROOT",
            explanation = "R - O - O - T spells ROOT! Roots drink up water from the soil.",
            difficulty = "Medium"
        ),
        Question(
            subject = "Science",
            unit = "Unit 4: Weather & Sky",
            questionType = "MULTIPLE_CHOICE",
            questionText = "What falls from dark rain clouds to water the trees and flowers?",
            hintImage = "🌧️",
            options = "Rain;Sand;Leaves;Feathers",
            correctAnswer = "Rain",
            explanation = "Raindrops fall from clouds and give fresh water to plants and animals!",
            difficulty = "Easy"
        )
    )
}
