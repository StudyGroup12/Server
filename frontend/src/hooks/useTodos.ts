import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  createTodo,
  createPersonalTodo,
  deleteTodo,
  deletePersonalTodo,
  fetchPersonalTodoDetail,
  fetchPersonalTodoProgress,
  fetchPersonalTodos,
  fetchTodoDetail,
  fetchTodoProgress,
  fetchTodos,
  updatePersonalTodo,
  updatePersonalTodoComplete,
  updateTodo,
  updateTodoComplete,
} from '../api/todo.api';
import { TodoFormData } from '../types/todo.types';

const PAGE_SIZE = 10;

export const useTodoList = (groupId: number, page: number, completed?: boolean) => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['todos', groupId, page, completed],
    queryFn: () => fetchTodos(groupId, { page, size: PAGE_SIZE, completed }),
    enabled: hasToken && Number.isFinite(groupId),
  });
};

export const useTodoProgress = (groupId: number) => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['todos', 'progress', groupId],
    queryFn: () => fetchTodoProgress(groupId),
    enabled: hasToken && Number.isFinite(groupId),
  });
};

export const useTodoDetail = (groupId: number, todoId: number | undefined) => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['todo', groupId, todoId],
    queryFn: () => fetchTodoDetail(groupId, todoId as number),
    enabled: hasToken && Number.isFinite(groupId) && todoId !== undefined && Number.isFinite(todoId),
  });
};

export const useCreateTodo = (groupId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: TodoFormData) => createTodo(groupId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todos', groupId] });
      queryClient.invalidateQueries({ queryKey: ['todos', 'progress', groupId] });
    },
  });
};

export const useUpdateTodo = (groupId: number, todoId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: TodoFormData) => updateTodo(groupId, todoId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todo', groupId, todoId] });
      queryClient.invalidateQueries({ queryKey: ['todos', groupId] });
      queryClient.invalidateQueries({ queryKey: ['todos', 'progress', groupId] });
    },
  });
};

export const useUpdateTodoComplete = (groupId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (params: { todoId: number; completed: boolean }) =>
      updateTodoComplete(groupId, params.todoId, params.completed),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['todo', groupId, response.data.id] });
      queryClient.invalidateQueries({ queryKey: ['todos', groupId] });
      queryClient.invalidateQueries({ queryKey: ['todos', 'progress', groupId] });
    },
  });
};

export const useDeleteTodo = (groupId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (todoId: number) => deleteTodo(groupId, todoId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['todos', groupId] });
      queryClient.invalidateQueries({ queryKey: ['todos', 'progress', groupId] });
    },
  });
};

export const usePersonalTodoList = (page: number, completed?: boolean) => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['personal-todos', page, completed],
    queryFn: () => fetchPersonalTodos({ page, size: PAGE_SIZE, completed }),
    enabled: hasToken,
  });
};

export const usePersonalTodoProgress = () => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['personal-todos', 'progress'],
    queryFn: fetchPersonalTodoProgress,
    enabled: hasToken,
  });
};

export const usePersonalTodoDetail = (todoId: number | undefined) => {
  const hasToken = !!sessionStorage.getItem('accessToken');
  return useQuery({
    queryKey: ['personal-todo', todoId],
    queryFn: () => fetchPersonalTodoDetail(todoId as number),
    enabled: hasToken && todoId !== undefined && Number.isFinite(todoId),
  });
};

export const useCreatePersonalTodo = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: TodoFormData) => createPersonalTodo(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['personal-todos'] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos', 'progress'] });
    },
  });
};

export const useUpdatePersonalTodo = (todoId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: TodoFormData) => updatePersonalTodo(todoId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['personal-todo', todoId] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos'] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos', 'progress'] });
    },
  });
};

export const useUpdatePersonalTodoComplete = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (params: { todoId: number; completed: boolean }) =>
      updatePersonalTodoComplete(params.todoId, params.completed),
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: ['personal-todo', response.data.id] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos'] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos', 'progress'] });
    },
  });
};

export const useDeletePersonalTodo = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (todoId: number) => deletePersonalTodo(todoId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['personal-todos'] });
      queryClient.invalidateQueries({ queryKey: ['personal-todos', 'progress'] });
    },
  });
};
