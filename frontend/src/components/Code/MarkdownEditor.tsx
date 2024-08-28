import React, { useState, useEffect, useRef } from 'react';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { nightOwl } from 'react-syntax-highlighter/dist/esm/styles/prism';
import './MarkdownRenderer.css'; // Import custom styles

interface MarkdownEditorProps {
    markdownText: string;
    setMarkdownText: React.Dispatch<React.SetStateAction<string>>;
}

const MarkdownEditor: React.FC<MarkdownEditorProps> = ({ markdownText, setMarkdownText }) => {
    const [isEditing, setIsEditing] = useState(true);
    const textareaRef = useRef<HTMLTextAreaElement>(null);

    const handleTextChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setMarkdownText(e.target.value);
    };

    useEffect(() => {
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto';
            textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
        }
    }, [markdownText, isEditing]);

    return (
        <div>
            {isEditing ? (
                <textarea
                    value={markdownText}
                    onChange={handleTextChange}
                    placeholder="Добавить описание"
                    className="textarea"
                    ref={textareaRef}
                />
            ) : (
                <ReactMarkdown
                    children={markdownText}
                    components={{
                        code({ node, inline, className, children, ...props }: any) {
                            const language = className?.replace('language-', '') || 'text';
                            return !inline ? (
                                <SyntaxHighlighter
                                    language={language}
                                    style={nightOwl}
                                    useInlineStyles={true}
                                    className="code-block"
                                    {...props}
                                >
                                    {String(children).replace(/\n$/, '')}
                                </SyntaxHighlighter>
                            ) : (
                                <code className={className} {...props}>
                                    {children}
                                </code>
                            );
                        },
                    }}
                />
            )}
            <button
                type="button" // Устанавливаем тип кнопки как 'button'
                className="ask__edit__button"
                onClick={() => setIsEditing(!isEditing)}
            >
                {isEditing ? 'Preview' : 'Edit'}
            </button>
        </div>
    );
};

export default MarkdownEditor;
