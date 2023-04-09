import NavBar from '../components/NavBar';
import Banner from '../components/Banner';
import ProjectCard from '../components/ProjectCard';
import WriteIcon from '../assets/image/pencil-square.svg';
import { useNavigate } from 'react-router-dom';

const MainPage = () => {
  const navigate = useNavigate(); // react-router-dom useNavigate 사용 선언

  function toWrite() {
    // react-router-dom을 이용한 글쓰기 페이지로 이동 함수
    navigate('/write');
  }
  return (
    <div className="relative h-screen w-screen bg-gray-50">
      <NavBar />
      <div>
        <Banner />
        <button onClick={goToWriting}
          className="fixed bottom-10 right-10 z-10 my-auto mb-2 flex shrink-0 flex-row items-center rounded-full
          bg-graphyblue px-4 py-1 pt-3 pb-3 font-semibold text-slate-50 drop-shadow-md
          sm:invisible"
          onClick={() => toWrite()}
        >
          <img className="mr-2 h-[20px] w-[20px]" src={WriteIcon} />
          <span className="shrink-0 font-semibold">프로젝트 공유</span>
        </button>
        <div className="ml-10 mb-5 pt-20 font-ng-b text-2xl">All</div>
        <div className="ml-8 flex w-11/12 flex-wrap justify-center gap-10">
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
          <ProjectCard />
        </div>
      </div>
    </div>
  );
};

export default MainPage;
