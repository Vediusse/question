// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HomePage from './pages/Index/HomePage';
import Questions from './pages/Question/Questions';
import QuestionDetails from './pages/Details/Details';
import Layout from './components/Layout/Layout';

const App: React.FC = () => {
    return (
        <Router>
            <Layout>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/questions" element={<Questions />} />
                    <Route path="/questions/:id" element={<QuestionDetails />} />
                </Routes>
            </Layout>

        </Router>

    );
};

export default App;
