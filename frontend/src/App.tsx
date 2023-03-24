import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import MainPage from './pages/MainPage';
import WritingPage from './pages/WritingPage';
import ReadingPage from './pages/ReadingPage';

function App() {
  return (
    <RecoilRoot>
      <Router>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/write" element={<WritingPage />} />
          <Route path="/read" element={<ReadingPage />} />
        </Routes>
      </Router>
    </RecoilRoot>
  );
}
export default App;
