import { yupResolver } from '@hookform/resolvers/yup';
import axios from 'axios';
import Cookies from 'js-cookie';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import * as yup from 'yup';

import Email from '../assets/image/email.svg';
import { accessTokenState } from '../Recoil';

type DataObject = {
  email: string;
  password: string;
};

const schema = yup.object().shape({
  email: yup
    .string()
    .email('이메일 형식이 맞지 않습니다.')
    .required('이메일이 필요합니다.'),
  password: yup
    .string()
    .min(8, '비밀번호는 최소 8자리 이상이여야 합니다.')
    .matches(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
      '1개 이상의 대소문자, 숫자, 특수문자가 포함되어야 합니다.',
    )
    .required('비밀번호가 필요합니다.'),
});

function Signin() {
  const navigate = useNavigate();
  const [, setAccessToken] = useRecoilState(accessTokenState);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<DataObject>({
    resolver: yupResolver(schema),
  });

  function toMain() {
    navigate('/');
  }

  function toSignup() {
    navigate('/signup');
  }

  const onSubmit: SubmitHandler<DataObject> = async (data: DataObject) => {
    try {
      const res = await axios.post(
        'http://localhost:8080/api/v1/members/login',
        {
          email: data.email,
          password: data.password,
        },
      );
      console.log(res);
      setAccessToken(res.data.accessToken); // accessToken recoil에 저장
      Cookies.set('refreshToken', res.data.refreshToken, {
        httpOnly: true, // httpOnly 쿠키에 refreshToken 저장
        // secure: true, // https에서만 작동
      });
      navigate('/');
    } catch (err: unknown) {
      console.log(err);
    }
  };

  return (
    <div className=" h-auto min-h-screen w-screen bg-zinc-200 pt-10 pb-10">
      <button
        onClick={() => toMain()}
        className="mx-auto mb-10 block font-lato text-5xl text-graphyblue"
        type="button"
      >
        Graphy
      </button>
      <div className="mx-auto flex h-auto w-[450px] flex-col rounded-xl border bg-white pt-12 ">
        <span className=" ml-8 font-ng-eb text-3xl text-graphyblue">
          로그인
        </span>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="mx-auto flex w-[360px] flex-col"
        >
          <input
            {...register('email')}
            type="email"
            className="mt-12 h-[60px] rounded-3xl border pl-4 text-lg"
            placeholder="이메일 주소"
            autoComplete="email"
          />
          <p className="text-sm">{errors.email?.message}</p>
          <input
            {...register('password')}
            type="password"
            className="mt-10 h-[60px] rounded-3xl border pl-4 text-lg"
            placeholder="비밀번호 (8자리 이상)"
            autoComplete="current-password"
          />
          <p className="text-sm">{errors.password?.message}</p>
          <button
            className=" mt-10 flex h-[60px] items-center rounded-3xl border bg-graphyblue"
            type="submit"
          >
            <img src={Email} alt="email" className="ml-4" />
            <span className=" mx-auto pr-7 font-ng-eb text-xl text-white">
              로그인
            </span>
          </button>
        </form>
        <div className="my-8 mx-auto flex">
          <div>
            <input type="checkbox" className="h-4 w-4" />
            <span className="ml-2 text-xl">로그인 상태 유지</span>
          </div>
          <button className="ml-16 mr-0 text-xl" type="button">
            비밀번호 찾기
          </button>
        </div>
        <div className="m-auto mb-0 flex h-20 w-[450px] items-center justify-center border-t">
          <span>아직 회원이 아니세요?</span>
          <button
            className="mx-4 font-ng-eb text-graphyblue"
            type="button"
            onClick={() => toSignup()}
          >
            회원가입
          </button>
        </div>
      </div>
    </div>
  );
}

export default Signin;
