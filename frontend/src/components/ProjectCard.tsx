import React from 'react';

const ProjectCard = () => {
  return (
    <div className="rounded-md drop-shadow-md">
      <div className="flex flex-col text-center w-187 h-150 bg-rose-100 rounded-t-lg">
        <div>우리 학교 동창회 서비스</div>
        <div>moyora</div>
      </div>

      <div className="flex w-187 h-76 bg-stone-50 rounded-b-lg">
        <div className="grow">
          <h1>Title</h1>
          <p>explain</p>
          <div>
            <span>#Spring</span>
            <span>#React</span>
            <span>#Typescript</span>
          </div>
        </div>
        {/* <div>좋아요/저장하기</div> */}
      </div>
    </div>
  );
};

export default ProjectCard;
